package ml.lacmus.lacmusandroid.domain

import android.content.Context
import android.util.Log
import com.davemorrissey.labs.subscaleview.ImageSource
import ml.lacmus.lacmusandroid.TAG
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State


class GetImageSourceUseCase(val context: Context) {
    private val bitmapUtils = BitmapUtils(context)

    fun execute(dronePhoto: DronePhoto): ImageSource {
        Log.d(TAG, "Draw boxes on Photo: $dronePhoto")
        return if (dronePhoto.state == State.HasPedestrian) {
            val mutableBmp = bitmapUtils.openBitmap(dronePhoto.uri)
            bitmapUtils.drawBoundingBoxes(mutableBmp, dronePhoto)
            ImageSource.bitmap(mutableBmp)
        } else {
            ImageSource.uri(dronePhoto.uri)
        }

    }


}