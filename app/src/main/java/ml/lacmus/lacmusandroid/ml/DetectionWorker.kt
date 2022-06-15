package ml.lacmus.lacmusandroid.ml

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import ml.lacmus.lacmusandroid.ui.SharedViewModel
import java.lang.Exception

class DetectionWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val sharedViewModel = SharedViewModel.getInstance(context as LacmusApplication)

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private val channelId = applicationContext.getString(R.string.notification_channel_id)
    private val title = applicationContext.getString(R.string.notification_title)
    private val cancel = applicationContext.getString(R.string.cancel_detection)

    override suspend fun doWork(): Result {
        val detector = TFLiteObjectDetectionAPIModel(applicationContext, MODEL_FILE)
        val items = inputData.keyValueMap.toSortedMap(compareBy { it.toInt() })
        val itemsCount = items.size
        val fi = createForegroundInfo("Start")
        setForeground(fi)
        for ((key, value) in items){
            val progress = "$key / $itemsCount"
            notificationManager.notify(NOTIFICATION_ID, createNotification(progress))

            val bboxes = detect(value.toString(), detector)
            sharedViewModel.updatePhotoDetection(key.toInt(), bboxes)
            if (isStopped) break
        }
        return Result.success()
    }

    private fun detect(imgUrl: String, detector: Detector): List<RectF> {
        Log.d(TAG, "Start detection with worker: $imgUrl")
        val bitmap = loadBitmap(imgUrl)
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
    private fun loadBitmap(uriString: String): Bitmap {
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

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(channelId, title)
        }
        val notification = createNotification(progress)
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createNotification(progress: String): Notification {
        // This PendingIntent can be used to cancel the worker
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.lacmus_logo)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_dialog_alert, cancel, cancelIntent)
            .build()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String): String {
        // Create a Notification channel
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(chan)
        return channelId
    }


    companion object {
        const val NOTIFICATION_ID = 42
        const val TAG = "DetectionWorker"
        const val WORK_TAG = "DetectionWorker"

    }
}