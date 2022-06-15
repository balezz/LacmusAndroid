package ml.lacmus.lacmusandroid

import android.app.Application
import ml.lacmus.lacmusandroid.data.DronePhotoRepository

class LacmusApplication : Application() {
    val dronePhotoRepository = DronePhotoRepository.getInstance(this)
}