package ml.lacmus.lacmusandroid.domain

import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.DronePhotoCacheRepository

class SetNewDronePhotosUseCase {
    private val dronePhotoCacheRepository = DronePhotoCacheRepository.getInstance()

    fun execute(newDronePhotos: List<DronePhoto>) {
        dronePhotoCacheRepository.setDronePhotos(newDronePhotos)
    }
}