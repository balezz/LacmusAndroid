package ml.lacmus.lacmusandroid.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import ml.lacmus.lacmusandroid.LacmusApplication
import java.lang.Exception


class DronePhotoRepository(private val application: LacmusApplication) {
    private var dronePhotos: List<DronePhoto> = listOf()


    fun setDronePhotos(newDronePhotos: List<DronePhoto>){
        dronePhotos = newDronePhotos
    }

    fun getDronePhotos(): List<DronePhoto>{
        return dronePhotos
    }

    fun getPhoto(index: Int): DronePhoto {
        return dronePhotos[index]
    }

    fun updatePhoto(index: Int, bboxes: List<RectF>){
        if (bboxes.isNotEmpty()){
            dronePhotos[index].state = State.HasPedestrian
            dronePhotos[index].bboxes = bboxes.toList()   // just create a copy
        }
        else{
            dronePhotos[index].state = State.NoPedestrian
        }
    }

    @Throws(Exception::class)
    private fun loadImage(uriString: String): Bitmap {
        var bitmap: Bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)
        val imageUri = Uri.parse(uriString)
        val contentResolver: ContentResolver = application.contentResolver
        try {
            val pfd = contentResolver.openFileDescriptor(imageUri, "r")
            bitmap = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }



    companion object {
        const val TAG = "DronePhotoRepository"

        private var instance:DronePhotoRepository? = null

        fun getInstance(application: LacmusApplication): DronePhotoRepository {
            if (instance == null) {
                instance = DronePhotoRepository(application)
            }
            return instance as DronePhotoRepository
        }
    }
}