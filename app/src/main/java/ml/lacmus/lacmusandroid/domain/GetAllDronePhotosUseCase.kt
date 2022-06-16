package ml.lacmus.lacmusandroid.domain

import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.DronePhotoCacheRepository

class GetAllDronePhotosUseCase {
    private val dronePhotoRepository = DronePhotoCacheRepository.getInstance()

    fun execute(): List<DronePhoto> {
        return dronePhotoRepository.getDronePhotos()
    }
}