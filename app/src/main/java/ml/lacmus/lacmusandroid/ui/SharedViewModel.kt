package ml.lacmus.lacmusandroid.ui

import android.content.Intent
import android.graphics.RectF
import androidx.lifecycle.*
import com.davemorrissey.labs.subscaleview.ImageSource
import ml.lacmus.lacmusandroid.*
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import ml.lacmus.lacmusandroid.domain.*
import java.lang.IllegalArgumentException
import java.util.*

class SharedViewModel(private val application: LacmusApplication): ViewModel() {
    private var hideEmptyPhotosFlag = false

    private val updateDronePhotoUseCase = UpdateDronePhotoUseCase()
    private val getAllDronePhotosUseCase = GetAllDronePhotosUseCase()
    private val setNewDronePhotosUseCase = SetNewDronePhotosUseCase()
    private val startDetectionUseCase = StartDetectionUseCase(application.applicationContext)
    private val getImageSourceUseCase = GetImageSourceUseCase(application.applicationContext)
    private val getSharePhotoIntentUseCase = GetSharePhotoIntentUseCase(
        application.applicationContext)

    val photos = MutableLiveData<List<DronePhoto>>()
    val updatedIndex = MutableLiveData(-1)
    var detectionIsDone = false

    fun setNewDronePhotos(uriList: List<String>) {
        val photoList = uriList.map { DronePhoto(it) }
        setNewDronePhotosUseCase.execute(photoList)
        photos.postValue(photoList)
    }

    fun getImageSource(index: Int): ImageSource {
        val dronePhoto = photos.value?.get(index)!!
        return getImageSourceUseCase.execute(dronePhoto)
    }

    fun updatePhotoDetection(itemChanged: Int, bboxes: List<RectF>){
        updateDronePhotoUseCase.execute(itemChanged, bboxes)
        updatedIndex.postValue(itemChanged)
    }

    fun showEmptyDronePhotos(){
        hideEmptyPhotosFlag = false
        photos.postValue(getAllDronePhotosUseCase.execute())
    }

    fun hideEmptyDronePhotos(){
        hideEmptyPhotosFlag = true
        val hiddenPhotoList = getAllDronePhotosUseCase.execute().filter {
            it.state != State.NoPedestrian }
        photos.postValue(hiddenPhotoList)
    }

    fun startDetectionWithWorker(uriStrList: List<String>): UUID {
        detectionIsDone = false
        return startDetectionUseCase.execute(uriStrList)
    }

    fun shareImage(imagePosition: Int): Intent {
        val dronePhoto = photos.value!![imagePosition]
        return getSharePhotoIntentUseCase.execute(dronePhoto)
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