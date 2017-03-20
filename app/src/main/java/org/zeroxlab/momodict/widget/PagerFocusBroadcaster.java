package org.zeroxlab.momodict.widget;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class PagerFocusBroadcaster implements ViewPager.OnPageChangeListener {

    private final FragmentStatePagerAdapter mAdapter;

    public PagerFocusBroadcaster(@NonNull FragmentStatePagerAdapter adapter) {
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
        Object item = mAdapter.getItem(pos);
        if ((item != null) && (item instanceof ViewPagerFocusable)) {
            ((ViewPagerFocusable) item).onViewPagerFocused();
        }
    }
}
