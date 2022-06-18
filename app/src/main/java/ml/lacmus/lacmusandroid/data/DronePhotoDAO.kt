package ml.lacmus.lacmusandroid.data

interface DronePhotoDAO {
    fun setDronePhotos(newDronePhotos: List<DronePhoto>)
    fun getDronePhotos(): List<DronePhoto>
    fun getDronePhoto(index: Int): DronePhoto
    fun setDronePhoto(index: Int, dronePhoto: DronePhoto)
}