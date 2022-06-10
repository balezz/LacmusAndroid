package ml.lacmus.lacmusandroid.ml

import android.app.Notification
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Trace
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import ml.lacmus.lacmusandroid.*
import ml.lacmus.lacmusandroid.R
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.DronePhotoRepository
import java.lang.Exception

class DetectionWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val photoRepository = DronePhotoRepository.getInstance(context as LacmusApplication)
    private val detector = TFLiteObjectDetectionAPIModel.createInstance(context, MODEL_FILE)

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        // Mark the Worker as important
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        for ((itemChanged, dronePhoto) in photoRepository.getDronePhotos().withIndex()) {
            dronePhoto.bboxes = detect(dronePhoto.uri)
        }
        return Result.success()
    }

    private fun detect(imgUrl: String): List<RectF> {
        Log.d(TAG, "Start detection with worker: $imgUrl")
        val bitmap = loadImage(imgUrl)
        return detectBoxes(bitmap, detector)

    }


    private fun detectBoxes(bigBitmap: Bitmap, detector: Detector): List<RectF> {
        Trace.beginSection("Detect boxes on full image")
        val bboxes = mutableListOf<RectF>()
        val scaledBitmap = Bitmap.createScaledBitmap(
            bigBitmap,
            NUM_CROPS_W * CROP_SIZE,
            NUM_CROPS_H * CROP_SIZE,
            false
        )
        for (h in 0 until NUM_CROPS_H) {
            for (w in 0 until NUM_CROPS_W) {
                val t0 = System.currentTimeMillis()
                val cropBmp = Bitmap.createBitmap(
                    scaledBitmap,
                    w * CROP_SIZE,
                    h * CROP_SIZE,
                    CROP_SIZE,
                    CROP_SIZE
                )
                val recognitions = detector.recognizeImage(cropBmp)
                for (rec in recognitions) {
                    val newBox = RectF(rec.location)
                    newBox.offset(
                        (w * CROP_SIZE).toFloat(),
                        (h * CROP_SIZE).toFloat()
                    )
                    bboxes.add(newBox)
                }
                Log.d(TAG, "Done crop detection: ${System.currentTimeMillis() - t0} ms")
            }
        }
        Trace.endSection()
        return bboxes
    }


    @Throws(Exception::class)
    private fun loadImage(uriString: String): Bitmap {
        var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val imageUri = Uri.parse(uriString)
        val contentResolver: ContentResolver = applicationContext.contentResolver
        try {
            val pfd = contentResolver.openFileDescriptor(imageUri, "r")
            bitmap = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel_download)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.lacmus_logo)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(Notification.DEFAULT_ALL, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create a Notification channel
    }

    companion object {
        const val TAG = "DetectionWorker"
    }
}