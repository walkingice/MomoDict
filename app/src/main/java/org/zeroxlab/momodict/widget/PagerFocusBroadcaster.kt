package org.zeroxlab.momodict.widget

import androidx.viewpager.widget.ViewPager
import org.zeroxlab.momodict.MainActivity

class PagerFocusBroadcaster(private val mAdapter: MainActivity.FragmentPagerAdapterImpl) : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    override fun onPageSelected(position: Int) = notifyViewPager(position)

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

    private fun notifyViewPager(pos: Int) {
        mAdapter.getFragment(pos)?.let { frg ->
            if (frg is ViewPagerFocusable) {
                frg.onViewPagerFocused()
            }
        }
    }
}
