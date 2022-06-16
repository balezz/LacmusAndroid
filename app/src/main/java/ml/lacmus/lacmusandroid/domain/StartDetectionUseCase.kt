package ml.lacmus.lacmusandroid.domain

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ml.lacmus.lacmusandroid.ml.DetectionWorker
import java.util.*

class StartDetectionUseCase(val context: Context) {

    fun execute(photoList: List<String>): UUID {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(DetectionWorker.WORK_TAG)

        val indexedMap = photoList.indices.map{ i -> i.toString() to photoList[i]}
        val dataBuilder = Data.Builder()
        for (pair in indexedMap) {
            dataBuilder.putString(pair.first, pair.second)
        }
        val inputData = dataBuilder.build()

        val detectionWorkRequest =
            OneTimeWorkRequestBuilder<DetectionWorker>()
                .setInputData(inputData)
                .addTag(DetectionWorker.WORK_TAG)
                .build()

        workManager.enqueue(detectionWorkRequest)
        return detectionWorkRequest.id
    }
}