package ml.lacmus.app.data

import android.graphics.Rect

data class DronePhoto(
    val uri: String,
    var state: State,
    var bboxes: List<Rect>
)