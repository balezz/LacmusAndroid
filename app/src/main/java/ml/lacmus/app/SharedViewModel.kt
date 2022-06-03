package ml.lacmus.app

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
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
    private val _photos = MutableLiveData<List<DronePhoto>>()
    val photos : LiveData<List<DronePhoto>> = _photos
    val updatedIndex = MutableLiveData(-1)

    fun postPhotos(uriList: MutableList<Uri>) {
        val photoList = uriList.map { DronePhoto(it.toString(), State.Unrecognized, listOf()) }
        Log.d(TAG, "Photo list size: ${photoList.size}")
        _photos.value = photoList
        Log.d(TAG, photos.toString() + this.toString())
        detectWithCoroutine(photoList)
    }

    fun getPhoto(index: Int): DronePhoto {
        Log.d(TAG, photos.toString()+ this.toString())
        return photos.value?.get(index)!!
    }

    private fun updatePhotoState(index: Int, bboxes: List<RectF>) {
        if (bboxes.isNotEmpty()){
            _photos.value?.also {
                it[index].state = State.HasPedestrian
                it[index].bboxes = bboxes.toList()   // just create a copy
            }
        }
        else
            _photos.value?.also {
                 it[index].state = State.NoPedestrian
            }
        updatedIndex.postValue(index)
    }

    private fun  detectWithCoroutine(photos: List<DronePhoto>){
        viewModelScope.launch(Dispatchers.Default) {
            val detector = getDetector()
            for ((itemChanged, dronePhoto) in photos.withIndex()) {
                val uriString = dronePhoto.uri
                val bitmap = loadImage(uriString)
                val bboxes = detectBoxes(bitmap, detector)
                updatePhotoState(itemChanged, bboxes)
            }
        }
    }

    private fun detectBoxes(bigBitmap: Bitmap, detector: Detector): List<RectF>{
        val bboxes = mutableListOf<RectF>()
        val scaledBitmap = Bitmap.createScaledBitmap(
            bigBitmap,
            NUM_CROPS_W * CROP_SIZE,
            NUM_CROPS_H * CROP_SIZE,
            false)

        for (h in 0 until NUM_CROPS_H){
            for (w in 0 until NUM_CROPS_W){
                val t0 = System.currentTimeMillis()
                Log.d(TAG, "Start cropping")
                val cropBmp = Bitmap.createBitmap(scaledBitmap,
                    w * CROP_SIZE,
                    h * CROP_SIZE,
                    CROP_SIZE,
                    CROP_SIZE)
                Log.d(TAG, "Done cropping, Start detection: ${System.currentTimeMillis() - t0} ms")
                val recognitions = detector.recognizeImage(cropBmp)
                for (rec in recognitions){
                    if (rec.confidence > CONFIDENCE_THRESHOLD) {
                        val newBox = RectF(rec.location)
                        newBox.offset(
                            (w * CROP_SIZE).toFloat(),
                            (h * CROP_SIZE).toFloat()
                        )
                        bboxes.add(newBox)
                    }
                }

                Log.d(TAG, "Done detection: ${System.currentTimeMillis() - t0} ms")
            }
        }
        return bboxes
    }

    private fun getDetector(): Detector {
        Log.d(TAG, "Get detector: $MODEL_FILE")
        return TFLiteObjectDetectionAPIModel.create(
            application,
            MODEL_FILE,
            LABEL_FILE,
            MODEL_INPUT_SIZE,
            IS_MODEL_QUANTIZED
        )
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