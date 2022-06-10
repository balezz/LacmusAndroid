package ml.lacmus.lacmusandroid.ui

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ml.lacmus.lacmusandroid.R
import ml.lacmus.lacmusandroid.LacmusApplication


class PhotoGridActivity : AppCompatActivity(R.layout.activity_main) {
    private val sViewModel: SharedViewModel by viewModels {
        SharedViewModel.SharedViewModelFactory(application!! as LacmusApplication)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val itemHide = menu.findItem(R.id.action_hide_empty)
            itemHide.isEnabled = sViewModel.detectionIsDone
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_hide_empty -> {
                if (item.isChecked) {
                    item.isChecked = false
                    sViewModel.showEmptyDronePhotos()
                } else{
                    item.isChecked = true
                    sViewModel.hideEmptyDronePhotos()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}