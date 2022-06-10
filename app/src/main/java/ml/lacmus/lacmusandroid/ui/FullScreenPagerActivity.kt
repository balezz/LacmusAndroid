package ml.lacmus.lacmusandroid.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ml.lacmus.lacmusandroid.*

class FullScreenPagerActivity : FragmentActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2
    private val sViewModel: SharedViewModel by viewModels {
        SharedViewModel.SharedViewModelFactory(application!! as LacmusApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_slide)
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager)
        val position = intent.getIntExtra(KEY_IMAGE_POSITION, 0)
        Log.d(TAG, "Get position: $position")
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(position, false)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = sViewModel.photos.value?.size!!

        override fun createFragment(position: Int): Fragment {
            val fragment = FullScreenPageFragment.create(position)
            Log.d(TAG, "Tapped position: $position")
            return fragment
        }
    }
}