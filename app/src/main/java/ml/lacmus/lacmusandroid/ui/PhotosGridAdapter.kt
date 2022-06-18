package ml.lacmus.lacmusandroid.ui

import android.content.Intent
import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ml.lacmus.lacmusandroid.KEY_IMAGE_POSITION
import ml.lacmus.lacmusandroid.TAG
import ml.lacmus.lacmusandroid.data.DronePhoto
import ml.lacmus.lacmusandroid.data.State
import ml.lacmus.lacmusandroid.databinding.GridViewItemBinding


class PhotosGridAdapter : ListAdapter<DronePhoto,
        PhotosGridAdapter.DronePhotoViewHolder>(DiffCallback){

    class DronePhotoViewHolder(private var binding: GridViewItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(dronePhoto: DronePhoto){
            binding.photo = dronePhoto
            binding.executePendingBindings()
            if (dronePhoto.state == State.Unrecognized) {
                binding.imageFrame.background.setTint(Color.WHITE)
            }
            if (dronePhoto.state == State.NoPedestrian) {
                binding.imageFrame.background.setTint(Color.GREEN)
            }
            if (dronePhoto.state == State.HasPedestrian) {
                binding.imageFrame.background.setTint(Color.RED)
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
            val context = it.context
            val intent = Intent(context, FullScreenPagerActivity::class.java)
            Log.d(TAG, "Put position: $position")
            intent.putExtra(KEY_IMAGE_POSITION, position)
            context.startActivity(intent)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DronePhoto>(){
        override fun areItemsTheSame(oldItem: DronePhoto, newItem: DronePhoto): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: DronePhoto, newItem: DronePhoto): Boolean {
            return oldItem.bboxes == newItem.bboxes
        }

    }



}