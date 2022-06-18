package ml.lacmus.lacmusandroid

import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun update_map_isCorrect() {
        val droneUris = listOf("uri1", "uri2", "uri3")
        val keys = droneUris.indices
        val values = droneUris.map { DronePhoto(it) }
        val dronePhotosMap = keys.zip(values).toMap()

        val dronePhoto = dronePhotosMap[1]
        dronePhoto?.state ?: State.NoPedestrian

        assertEquals(dronePhotosMap[1]?.state, dronePhoto?.state)
    }
}
