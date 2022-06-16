package ml.lacmus.lacmusandroid.data

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*


class DronePhotoCacheRepository : DronePhotoDAO {
    private val dronePhotos = mutableListOf<DronePhoto>()

    override fun setDronePhotos(newDronePhotos: List<DronePhoto>){
        dronePhotos.clear()
        dronePhotos.addAll(newDronePhotos)
    }

    override fun getDronePhotos(): List<DronePhoto> {
        return dronePhotos
    }

    override fun getDronePhoto(index: Int): DronePhoto {
        return dronePhotos[index]
    }

    override fun setDronePhoto(index: Int, dronePhoto: DronePhoto){

    }



    companion object {
        const val TAG = "DronePhotoRepository"

        private var instance:DronePhotoCacheRepository? = null

        fun getInstance(): DronePhotoCacheRepository {
            if (instance == null) {
                instance = DronePhotoCacheRepository()
            }
            return instance as DronePhotoCacheRepository
        }
    }
}