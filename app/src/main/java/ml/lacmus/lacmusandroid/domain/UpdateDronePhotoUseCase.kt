package ml.lacmus.lacmusandroid.domain

import android.graphics.RectF
import ml.lacmus.lacmusandroid.data.DronePhotoCacheRepository
import ml.lacmus.lacmusandroid.data.State

class UpdateDronePhotoUseCase {
    private val dronePhotoRepository = DronePhotoCacheRepository.getInstance()

    fun execute(index: Int, bboxes: List<RectF>) {
        val dronePhoto = dronePhotoRepository.getDronePhoto(index)
        if (bboxes.isNotEmpty()) {
            dronePhoto.state = State.HasPedestrian
            dronePhoto.bboxes = bboxes.toList()   // just create a copy
        }
        else {
            dronePhoto.state = State.NoPedestrian
        }
        dronePhotoRepository.setDronePhoto(index, dronePhoto)
    }
}
