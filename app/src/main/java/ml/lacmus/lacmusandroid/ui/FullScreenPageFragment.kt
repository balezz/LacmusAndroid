package ml.lacmus.lacmusandroid.ui

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Trace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.davemorrissey.labs.subscaleview.ImageSource
import ml.lacmus.lacmusandroid.*
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import ml.lacmus.lacmusandroid.databinding.FragmentScreenSlidePageBinding


class FullScreenPageFragment : Fragment() {

    private var _binding: FragmentScreenSlidePageBinding? = null
    private val binding get() = _binding!!
    private val sViewModel: SharedViewModel by viewModels {
        SharedViewModel.SharedViewModelFactory(activity?.application!! as LacmusApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreenSlidePageBinding.inflate(inflater, container, false)
        val imagePosition = requireArguments().getInt(KEY_IMAGE_POSITION)
        val dronePhoto = sViewModel.getPhoto(imagePosition)
        if (dronePhoto.state == State.HasPedestrian){
            drawBoxes(dronePhoto)
        } else {
            binding.fullscreenContent.setImage(ImageSource.uri(dronePhoto.uri))
        }
        return binding.root
    }

    private fun drawBoxes(dronePhoto: DronePhoto) {
        Log.d(TAG, "Draw boxes on Photo: $dronePhoto")
        Trace.beginSection("Draw bounding boxes")
        val pfd = requireContext().contentResolver.openFileDescriptor(Uri.parse(dronePhoto.uri), "r")
        val options = BitmapFactory.Options()
        options.inMutable = true
        val mutableBmp = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor, null, options)
        val xScale = mutableBmp.width.toFloat() / (NUM_CROPS_W * CROP_SIZE)
        val yScale = mutableBmp.height.toFloat() / (NUM_CROPS_H * CROP_SIZE)
        val canvas = Canvas(mutableBmp)
        if (!dronePhoto.bboxes.isNullOrEmpty()) {
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            for (bb in dronePhoto.bboxes){
                val scBox = RectF(
                    bb.left*xScale,
                    bb.top*yScale,
                    bb.right*xScale,
                    bb.bottom*yScale
                )
                canvas.drawRect(scBox, paint)
            }
        }
        binding.fullscreenContent.maxScale = 10f
        binding.fullscreenContent.setImage(ImageSource.bitmap(mutableBmp))
        Trace.endSection()
    }

    companion object{
        fun create(position: Int): FullScreenPageFragment {
            val fragment = FullScreenPageFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_IMAGE_POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

}