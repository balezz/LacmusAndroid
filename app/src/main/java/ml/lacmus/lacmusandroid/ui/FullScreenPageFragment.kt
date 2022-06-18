package ml.lacmus.lacmusandroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ml.lacmus.lacmusandroid.*
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
        binding.fullscreenContent.maxScale = 10f
        val imagePosition = requireArguments().getInt(KEY_IMAGE_POSITION)
        val imageSource = sViewModel.getImageSource(imagePosition)
        binding.fullscreenContent.setImage(imageSource)

        binding.photoShareButton.setOnClickListener {
            val intent = sViewModel.shareImage(imagePosition)
            startActivity(intent)
        }
        return binding.root
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