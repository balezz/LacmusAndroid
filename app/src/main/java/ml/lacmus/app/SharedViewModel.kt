package ml.lacmus.app

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Trace
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ml.lacmus.app.data.DronePhoto
import ml.lacmus.app.data.State
import ml.lacmus.app.ml.Detector
import ml.lacmus.app.ml.TFLiteObjectDetectionAPIModel
import java.lang.Exception
import java.lang.IllegalArgumentException

class SharedViewModel(private val application: LacmusApplication): ViewModel() {
    private var hideEmptyPhotosFlag = false
    private var photoList = listOf<DronePhoto>()
    private val _photos = MutableLiveData<List<DronePhoto>>()
    val photos : LiveData<List<DronePhoto>> = _photos
    val updatedIndex = MutableLiveData(-1)
    var detectionIsDone = false

    fun postPhotos(uriList: MutableList<Uri>) {
        photoList = uriList.map { DronePhoto(it.toString()) }
        Log.d(TAG, "Photo list size: ${photoList.size}")
        _photos.postValue(photoList.toList())                  // create copy
        detectWithCoroutine(photoList)
    }

    fun getPhoto(index: Int): DronePhoto {
        Log.d(TAG, photos.toString()+ this.toString())
        return photos.value?.get(index)!!
    }

    private fun updatePhotoState(index: Int, bboxes: List<RectF>) {
        if (bboxes.isNotEmpty()){
            photoList[index].state = State.HasPedestrian
            photoList[index].bboxes = bboxes.toList()   // just create a copy
        }
        else{
            photoList[index].state = State.NoPedestrian
        }
        updatedIndex.postValue(index)
        _photos.postValue(photoList)
    }

    private fun  detectWithCoroutine(photos: List<DronePhoto>){
        Log.d(TAG, "Start detection: $photos")
        viewModelScope.launch(Dispatchers.Default) {
            val detector = getDetector()
            detectionIsDone = false
            for ((itemChanged, dronePhoto) in photos.withIndex()) {
                val uriString = dronePhoto.uri
                val bitmap = loadImage(uriString)
                val bboxes = detectBoxes(bitmap, detector)
                updatePhotoState(itemChanged, bboxes)
            }
//            detector.close()
            detectionIsDone = true
        }
    }

    fun showEmptyDronePhotos(){
        hideEmptyPhotosFlag = false
        _photos.postValue(photoList)
    }

    fun hideEmptyDronePhotos(){
        hideEmptyPhotosFlag = true
        photos.value?.let { photoList = it.toList() }
        val hiddenPhotoList = _photos.value?.filter { it.state != State.NoPedestrian }
        _photos.postValue(hiddenPhotoList)
    }

    private fun detectBoxes(bigBitmap: Bitmap, detector: Detector): List<RectF>{
        Trace.beginSection("Detect boxes on full image")
        val bboxes = mutableListOf<RectF>()
        val scaledBitmap = Bitmap.createScaledBitmap(
            bigBitmap,
            NUM_CROPS_W * CROP_SIZE,
            NUM_CROPS_H * CROP_SIZE,
            false)

        for (h in 0 until NUM_CROPS_H){
            for (w in 0 until NUM_CROPS_W){
                val t0 = System.currentTimeMillis()
                val cropBmp = Bitmap.createBitmap(scaledBitmap,
                    w * CROP_SIZE,
                    h * CROP_SIZE,
                    CROP_SIZE,
                    CROP_SIZE)
                val recognitions = detector.recognizeImage(cropBmp)
                for (rec in recognitions){
                    val newBox = RectF(rec.location)
                    newBox.offset(
                        (w * CROP_SIZE).toFloat(),
                        (h * CROP_SIZE).toFloat()
                    )
                    bboxes.add(newBox)
                }
                Log.d(TAG, "Done crop detection: ${System.currentTimeMillis() - t0} ms")
            }
        }
        Trace.endSection()
        return bboxes
    }

    private fun getDetector(): Detector {
        Log.d(TAG, "Get detector: $MODEL_FILE, " +
                "threshold = $CONFIDENCE_THRESHOLD, " +
                "input size = $MODEL_INPUT_SIZE")
        return TFLiteObjectDetectionAPIModel.getInstance(application, MODEL_FILE)
    }

    @Throws(Exception::class)
    private fun loadImage(uriString: String): Bitmap {
        var bitmap: Bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)
        val imageUri = Uri.parse(uriString)
        val contentResolver: ContentResolver = application.contentResolver
        try {
            val pfd = contentResolver.openFileDescriptor(imageUri, "r")
            bitmap = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
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