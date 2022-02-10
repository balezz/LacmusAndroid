package ml.lacmus.app.ui

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ml.lacmus.app.*
import ml.lacmus.app.data.DronePhoto
import ml.lacmus.app.databinding.FragmentPhotoBinding


class FullScreenPhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!
    private val sViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(activity?.application!! as LacmusApplication)
    }
    private lateinit var dronePhoto: DronePhoto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val imagePosition = it.getInt(KEY_IMAGE_POSITION)
            Log.d(TAG, "Got Image: $imagePosition")
            dronePhoto = sViewModel.photos.value?.get(imagePosition)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawBoxes()
    }

    private fun drawBoxes() {
        val pfd = requireContext().contentResolver.openFileDescriptor(Uri.parse(dronePhoto.uri), "r")
        val bmp = BitmapFactory.decodeFileDescriptor(pfd?.fileDescriptor)
        val mutableBmp = Bitmap.createScaledBitmap(
            bmp,
            NUM_CROPS_W * CROP_SIZE,
            NUM_CROPS_H * CROP_SIZE,
            false)
        val canvas = Canvas(mutableBmp)
        val bboxes = dronePhoto.bboxes
        if (!bboxes.isNullOrEmpty()) {
            val paint = Paint()
            paint.color = Color.GREEN
            paint.style = Paint.Style.STROKE
            for (bb in bboxes){
                canvas.drawRect(bb, paint)
            }
        }
        binding.fullscreenContent.setImageBitmap(mutableBmp)
    }

}