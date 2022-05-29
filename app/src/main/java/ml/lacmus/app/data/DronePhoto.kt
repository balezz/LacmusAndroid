package ml.lacmus.app.data

import android.graphics.RectF

data class DronePhoto(
    val uri: String,
    var state: State,
    var bboxes: List<RectF>
)