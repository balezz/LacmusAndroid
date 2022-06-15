package ml.lacmus.lacmusandroid.ui

import android.graphics.RectF
import androidx.lifecycle.*
import ml.lacmus.lacmusandroid.*
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import java.lang.IllegalArgumentException

class SharedViewModel(private val application: LacmusApplication): ViewModel() {
    private var hideEmptyPhotosFlag = false
    private val photosRepo = application.dronePhotoRepository

    val photos = MutableLiveData<List<DronePhoto>>()
    val updatedIndex = MutableLiveData(-1)
    var detectionIsDone = false

    fun initPhotosList(uriList: List<String>) {
        val photoList = uriList.map { DronePhoto(it) }
        photosRepo.setDronePhotos(photoList)
        photos.postValue(photoList)
    }

    fun getPhoto(index: Int) = photosRepo.getPhoto(index)

    fun updatePhotoDetection(itemChanged: Int, bboxes: List<RectF>){
        photosRepo.updatePhotoDetection(itemChanged, bboxes)
        updatedIndex.postValue(itemChanged)
    }

    fun showEmptyDronePhotos(){
        hideEmptyPhotosFlag = false
        photos.postValue(photosRepo.getDronePhotos())
    }

    fun hideEmptyDronePhotos(){
        hideEmptyPhotosFlag = true
        val hiddenPhotoList = photosRepo.getDronePhotos()?.filter {
            it.state != State.NoPedestrian }
        photos.postValue(hiddenPhotoList)
    }


    class SharedViewModelFactory(private val application: LacmusApplication)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                SharedViewModel.getInstance(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        companion object {
            private var instance: SharedViewModelFactory? = null
            fun getInstance(application: LacmusApplication){
                instance ?: synchronized(SharedViewModelFactory::class.java) {
                    instance ?: SharedViewModelFactory(application).also { instance = it }
                }
            }
        }
    }

    companion object{
        private const val TAG = "SharedViewModel"
        private var instance: SharedViewModel? = null
        fun getInstance(application: LacmusApplication) = instance ?: synchronized(SharedViewModel::class.java){
            instance ?: SharedViewModel(application).also { instance = it }
        }
    }

}