package ml.lacmus.lacmusandroid

const val TAG = "LacmusApplication"
const val KEY_IMAGE_POSITION = "imagePosition"
const val KEY_IMAGE_URIS = "imageUris"
const val KEY_OUT_BOXES = "outBoxes"

// val model_inputs = mapOf(0 to 320, 1 to 384, 2 to 448, 3 to 512)

const val NUM_CROPS_W = 4
const val NUM_CROPS_H = 3

const val CROP_SIZE = 320
const val MODEL_FILE = "effdetB0_v0.tflite"

const val MODEL_INPUT_SIZE = CROP_SIZE
const val CONFIDENCE_THRESHOLD = 0.3f

const val NUM_DETECTIONS = 4

