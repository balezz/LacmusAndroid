package ml.lacmus.app

const val TAG = "LacmusApplication"

const val KEY_IMAGE_POSITION = "imagePosition"
const val CONFIDENCE_THRESHOLD = 0.3f

const val NUM_CROPS_W = 4
const val NUM_CROPS_H = 3
const val CROP_SIZE = 224

const val MODEL_INPUT_SIZE = CROP_SIZE
const val IS_MODEL_QUANTIZED = true
const val MODEL_FILE = "detectorB0_no_optims.tflite"
