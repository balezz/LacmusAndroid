package ml.lacmus.app.ui

import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ml.lacmus.app.TAG
import ml.lacmus.app.data.DronePhoto
import ml.lacmus.app.data.State
import ml.lacmus.app.databinding.GridViewItemBinding


class PhotosGridAdapter : ListAdapter<DronePhoto,
        PhotosGridAdapter.DronePhotoViewHolder>(DiffCallback){

    class DronePhotoViewHolder(private var binding: GridViewItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(dronePhoto: DronePhoto){
            binding.photo = dronePhoto
            binding.executePendingBindings()
            if (dronePhoto.state == State.NoPedestrian)
                binding.droneImage.setColorFilter(Color.LTGRAY, PorterDuff.Mode.LIGHTEN)
            if (dronePhoto.state == State.HasPedestrian) {
                binding.droneImage.setColorFilter(Color.GREEN, PorterDuff.Mode.LIGHTEN)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DronePhotoViewHolder {
        return DronePhotoViewHolder(
            GridViewItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: DronePhotoViewHolder, position: Int) {
        val dronePhoto = getItem(position)
        Log.d(TAG, "Adapter: ${dronePhoto.uri}")
        holder.bind(dronePhoto)
        holder.itemView.setOnClickListener{
            val action = PhotosGridFragmentDirections.actionGridFragmentToPhotoFragment(
                imagePosition = position)
            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DronePhoto>(){
        override fun areItemsTheSame(oldItem: DronePhoto, newItem: DronePhoto): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: DronePhoto, newItem: DronePhoto): Boolean {
            return oldItem.uri == newItem.uri
        }

    }

}