package ml.lacmus.lacmusandroid.data

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ml.lacmus.lacmusandroid.LacmusApplication


class DronePhotoRepository(private val application: LacmusApplication) : DronePhotoDAO {
    private var _dronePhotos = MutableLiveData<List<DronePhoto>>()
    val dronePhotos : LiveData<List<DronePhoto>> = _dronePhotos

    override fun setDronePhotos(newDronePhotos: List<DronePhoto>){
        _dronePhotos.postValue(newDronePhotos)
    }

    override fun getDronePhotos(): List<DronePhoto>? {
        return dronePhotos.value
    }

    override fun getPhoto(index: Int): DronePhoto? {
        return dronePhotos.value?.get(index)
    }

    override fun updatePhotoDetection(index: Int, bboxes: List<RectF>){
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