package ml.lacmus.lacmusandroid.domain

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Trace
import android.util.Log
import com.davemorrissey.labs.subscaleview.ImageSource
import ml.lacmus.lacmusandroid.CROP_SIZE
import ml.lacmus.lacmusandroid.NUM_CROPS_H
import ml.lacmus.lacmusandroid.NUM_CROPS_W
import ml.lacmus.lacmusandroid.TAG
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State

class GetImageSourceUseCase(val context: Context) {

    fun execute(dronePhoto: DronePhoto): ImageSource {
        Log.d(TAG, "Draw boxes on Photo: $dronePhoto")
        if (dronePhoto.state == State.HasPedestrian) {
            val mutableBmp = openBitmap(dronePhoto.uri)
            val xScale = mutableBmp.width.toFloat() / (NUM_CROPS_W * CROP_SIZE)
            val yScale = mutableBmp.height.toFloat() / (NUM_CROPS_H * CROP_SIZE)
            val canvas = Canvas(mutableBmp)
            if (!dronePhoto.bboxes.isNullOrEmpty()) {
                val paint = Paint()
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                for (bb in dronePhoto.bboxes) {
                    val scBox = RectF(
                        bb.left * xScale,
                        bb.top * yScale,
                        bb.right * xScale,
                        bb.bottom * yScale
                    )
                    canvas.drawRect(scBox, paint)
                }
            }
            return ImageSource.bitmap(mutableBmp)
        }
         else {
            return ImageSource.uri(dronePhoto.uri)
         }

    }

    private fun openBitmap(uri: String): Bitmap{
        val pfd = context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
        val options = BitmapFactory.Options()
        options.inMutable = true
        return BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor, null, options)
    }
}