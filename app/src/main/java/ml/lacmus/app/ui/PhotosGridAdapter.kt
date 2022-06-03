package ml.lacmus.app.ui

import android.content.Intent
import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ml.lacmus.app.KEY_IMAGE_POSITION
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
            if (dronePhoto.state == State.Unrecognized) {
//              binding.droneImage.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN)
                binding.imageFrame.background.setTint(Color.WHITE)
            }
            if (dronePhoto.state == State.NoPedestrian) {
//              binding.droneImage.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN)
                binding.imageFrame.background.setTint(Color.GREEN)
            }
            if (dronePhoto.state == State.HasPedestrian) {
                binding.imageFrame.background.setTint(Color.RED)
//                binding.droneImage.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN)
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
            val intent = Intent(context, ScreenSlidePagerActivity::class.java)
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