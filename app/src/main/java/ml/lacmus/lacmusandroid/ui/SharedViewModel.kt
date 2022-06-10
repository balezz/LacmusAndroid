package ml.lacmus.lacmusandroid.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Trace
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ml.lacmus.lacmusandroid.*
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import ml.lacmus.lacmusandroid.ml.DetectionWorker
import ml.lacmus.lacmusandroid.ml.Detector
import ml.lacmus.lacmusandroid.ml.TFLiteObjectDetectionAPIModel
import java.lang.Exception
import java.lang.IllegalArgumentException

class SharedViewModel(private val application: LacmusApplication): ViewModel() {
    private var hideEmptyPhotosFlag = false
    private val photosRepo = application.dronePhotoRepository
    private val workManager = WorkManager.getInstance(application.applicationContext)
    private val _photos = MutableLiveData<List<DronePhoto>>()
    val photos : LiveData<List<DronePhoto>> = _photos
    val updatedIndex = MutableLiveData(-1)
    var detectionIsDone = false

    fun updatePhotosList(uriList: MutableList<Uri>) {
        val photoList = uriList.map { DronePhoto(it.toString()) }
        photosRepo.setDronePhotos(photoList)
        Log.d(TAG, "Photo list size: ${photoList.size}")
        _photos.postValue(photoList)                  // create copy
        detectWithWorker()
    }

    fun getPhoto(index: Int) = photosRepo.getPhoto(index)

    private fun updatePhotoState(index: Int, bboxes: List<RectF>) {
        photosRepo.updatePhoto(index, bboxes)
        updatedIndex.postValue(index)
        _photos.postValue(photosRepo.getDronePhotos())
    }

    private fun detectWithWorker() {
        val detectionWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<DetectionWorker>()
                .build()
        workManager.enqueue(detectionWorkRequest)
        // listen detection result here
    }

    fun showEmptyDronePhotos(){
        hideEmptyPhotosFlag = false
        _photos.postValue(photosRepo.getDronePhotos())
    }

    fun hideEmptyDronePhotos(){
        hideEmptyPhotosFlag = true
        val hiddenPhotoList = photosRepo.getDronePhotos().filter {
            it.state != State.NoPedestrian }
        _photos.postValue(hiddenPhotoList)
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