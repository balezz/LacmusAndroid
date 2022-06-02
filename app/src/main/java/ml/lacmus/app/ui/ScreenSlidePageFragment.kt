package ml.lacmus.app.ui

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.davemorrissey.labs.subscaleview.ImageSource
import ml.lacmus.app.*
import ml.lacmus.app.databinding.FragmentScreenSlidePageBinding


class ScreenSlidePageFragment : Fragment() {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawBoxes()
    }

    private fun drawBoxes() {
        val imagePosition = requireArguments().getInt(KEY_IMAGE_POSITION, 0)
        val dronePhoto = sViewModel.getPhoto(imagePosition)
        Log.d(TAG, "Photo: $dronePhoto")
        val pfd = requireContext().contentResolver.openFileDescriptor(Uri.parse(dronePhoto.uri), "r")
        val bmp = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
        val mutableBmp = Bitmap.createScaledBitmap(
            bmp,
            NUM_CROPS_W * CROP_SIZE,
            NUM_CROPS_H * CROP_SIZE,
            false)
        val canvas = Canvas(mutableBmp)
        val bboxes = dronePhoto?.bboxes
        if (!bboxes.isNullOrEmpty()) {
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            for (bb in bboxes){
                canvas.drawRect(bb, paint)
            }
        }
        binding.fullscreenContent.maxScale = 10f
        binding.fullscreenContent.setImage(ImageSource.bitmap(mutableBmp))
    }

}