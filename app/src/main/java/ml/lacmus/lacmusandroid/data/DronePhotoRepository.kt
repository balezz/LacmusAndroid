package ml.lacmus.lacmusandroid.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ml.lacmus.lacmusandroid.LacmusApplication
import java.lang.Exception
import java.util.*


class DronePhotoRepository(private val application: LacmusApplication) {
    private var _dronePhotos = MutableLiveData<List<DronePhoto>>()
    val dronePhotos : LiveData<List<DronePhoto>> = _dronePhotos


    fun setDronePhotos(newDronePhotos: List<DronePhoto>){
        _dronePhotos.postValue(newDronePhotos)
    }

    fun getDronePhotos(): List<DronePhoto>? {
        return dronePhotos.value
    }

    fun getPhoto(index: Int): DronePhoto? {
        return dronePhotos.value?.get(index)
    }

    fun updatePhoto(index: Int, bboxes: List<RectF>){
        val dronePhoto = dronePhotos.value?.get(index)
        if (bboxes.isNotEmpty()){
            if (dronePhoto != null) {
                dronePhoto.state = State.HasPedestrian
                dronePhoto.bboxes = bboxes.toList()   // just create a copy
            }
        }
        else{
            if (dronePhoto != null) {
                dronePhoto.state = State.NoPedestrian
            }
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