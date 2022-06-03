package ml.lacmus.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ml.lacmus.app.LacmusApplication
import ml.lacmus.app.SharedViewModel
import ml.lacmus.app.databinding.FragmentGridBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PhotosGridFragment : Fragment() {

    private var _binding: FragmentGridBinding? = null
    private val binding get() = _binding!!

    private val sViewModel: SharedViewModel by viewModels {
        SharedViewModel.SharedViewModelFactory(activity?.application!! as LacmusApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGridBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener {
            dispatchOpenImagesIntent()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PhotosGridAdapter()
        binding.photosGrid.adapter = adapter
        sViewModel.photos.observe(viewLifecycleOwner){
            binding.textviewFirst.visibility = View.GONE
            adapter.submitList(it)
        }
        sViewModel.updatedIndex.observe(viewLifecycleOwner){
            adapter.notifyItemChanged(it)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.photosGrid.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dispatchOpenImagesIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        getImages.launch(intent)
    }

    private val getImages = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val intent = activityResult.data
        val uriList = mutableListOf<Uri>()

        intent?.let {
            if (it.clipData != null) {
                val itemCount = it.clipData!!.itemCount
                for (i in 0 until itemCount) {
                    uriList.add(it.clipData!!.getItemAt(i).uri)
                }
            } else if (it.data != null) {
                uriList.add(it.data!!)
            }
            Log.d(TAG, "Get from gallery uri list: $uriList")
            sViewModel.postPhotos(uriList)
        }
    }


    companion object {
        const val TAG = "PhotosGridFragment"
    }

}