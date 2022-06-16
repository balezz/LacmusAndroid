package ml.lacmus.lacmusandroid

import android.app.Application
import ml.lacmus.lacmusandroid.data.DronePhotoCacheRepository

class LacmusApplication : Application() {
    val dronePhotoRepository = DronePhotoCacheRepository.getInstance()
}