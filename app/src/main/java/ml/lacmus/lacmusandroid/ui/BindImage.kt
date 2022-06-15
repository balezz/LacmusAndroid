package ml.lacmus.lacmusandroid.ui

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import ml.lacmus.lacmusandroid.R
import ml.lacmus.lacmusandroid.TAG

@BindingAdapter("imageUri")
fun bindImage(imgView: ImageView, imgUri: String?) {
    Log.d(TAG, "Uri: $imgUri")
    imgUri?.let {
        imgView.load(imgUri.toUri()){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}
