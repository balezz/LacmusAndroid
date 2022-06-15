package ml.lacmus.lacmusandroid.data

import android.graphics.RectF

interface DronePhotoDAO {
    fun setDronePhotos(newDronePhotos: List<DronePhoto>)
    fun getDronePhotos(): List<DronePhoto>?
    fun getPhoto(index: Int): DronePhoto?
    fun updatePhotoDetection(index: Int, bboxes: List<RectF>)
}