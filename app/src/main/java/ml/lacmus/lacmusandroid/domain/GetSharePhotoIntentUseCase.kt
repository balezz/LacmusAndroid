package ml.lacmus.lacmusandroid.domain

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import ml.lacmus.lacmusandroid.TAG
import ml.lacmus.lacmusandroid.data.DronePhoto
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI

class GetSharePhotoIntentUseCase(val context: Context) {
    private val bitmapUtils = BitmapUtils(context)

    fun execute(dronePhoto: DronePhoto): Intent {
        val mutableBmp = bitmapUtils.openBitmap(dronePhoto.uri)
        bitmapUtils.drawBoundingBoxes(mutableBmp, dronePhoto)
        val savedURI = saveTempImage(mutableBmp)
        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(dronePhoto.uri))
        shareIntent.putExtra(Intent.EXTRA_STREAM, savedURI)
        return shareIntent
    }

    private fun saveTempImage(mutableBmp: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        val file = File(context.filesDir, "shared.jpg")
        Log.d(TAG, "Save shared image: $file")
        mutableBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        try{
            file.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
        } catch (e: IOException){
            e.printStackTrace()
        }
        return file.toUri()
    }
}