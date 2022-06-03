package ml.lacmus.app.data

import android.graphics.RectF

data class DronePhoto(
    val uri: String,
    var state: State,
    var bboxes: List<RectF>
) {
    constructor(uri: String) : this(uri, State.Unrecognized, listOf())
}