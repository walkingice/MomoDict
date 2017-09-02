package org.zeroxlab.momodict.widget;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import org.zeroxlab.momodict.MainActivity;

public class PagerFocusBroadcaster implements ViewPager.OnPageChangeListener {

    private final MainActivity.FragmentPagerAdapterImpl mAdapter;

    public PagerFocusBroadcaster(@NonNull MainActivity.FragmentPagerAdapterImpl adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        notifyViewPager(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void notifyViewPager(int pos) {
        Fragment item = mAdapter.getFragment(pos);
        if ((item != null) && (item instanceof ViewPagerFocusable)) {
            ((ViewPagerFocusable) item).onViewPagerFocused();
        }
    }
}
