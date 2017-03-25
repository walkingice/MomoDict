package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.LibsBuilder;

import org.zeroxlab.momodict.ui.HistoryFragment;
import org.zeroxlab.momodict.ui.InputFragment;
import org.zeroxlab.momodict.ui.MemoFragment;
import org.zeroxlab.momodict.widget.BackKeyHandler;
import org.zeroxlab.momodict.widget.PagerFocusBroadcaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity, it consists several tabs which present by ViewPager.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = Momodict.TAG;
    private static final int REQ_CODE_IMPORT = 0x1002;

    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_viewpager);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.menu_import, Menu.NONE, "Import dictionary");
        menu.add(Menu.NONE, R.id.menu_clear_history, Menu.NONE, "Clear History");
        menu.add(Menu.NONE, R.id.menu_license, Menu.NONE, "License");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import:
                onImportClicked();
                return true;
            case R.id.menu_clear_history:
                onClearHistory();
                return true;
            case R.id.menu_license:
                onShowLicenses();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case REQ_CODE_IMPORT:
                onResultImport(resultCode, data);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // 1. if user not in first tab, move to first tab
        // 2. if the first tab can handle back key, do nothing here
        // 3. otherwise, call super.onBackPressed(usually close this activity)
        if (mPager.getCurrentItem() != 0) {
            mPager.setCurrentItem(0);
        } else {
            Object first = mAdapter.getItem(0);
            if (first instanceof BackKeyHandler) {
                BackKeyHandler handler = (BackKeyHandler) first;
                if (handler.backKeyHandled()) {
                    return;
                }
            }
            super.onBackPressed();
        }
    }

    private void initView() {
        // To create fragments for Tabs, and manage them by MyPagerAdapter
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final FragmentManager mgr = getSupportFragmentManager();
        mAdapter = new MyPagerAdapter(mgr);
        mAdapter.addFragment(new InputFragment(), "Input");
        mAdapter.addFragment(new HistoryFragment(), "History");
        mAdapter.addFragment(new MemoFragment(), "Memo");

        mPager = (ViewPager) findViewById(R.id.fragment_container);
        mPager.setAdapter(mAdapter);
        // To notify fragment, when page-change happens
        mPager.addOnPageChangeListener(new PagerFocusBroadcaster(mAdapter));
        tabs.setupWithViewPager(mPager);

        //init action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        toolbar.setNavigationIcon(R.mipmap.ic_logo);
        setSupportActionBar(toolbar);
    }

    /**
     * Callback when user click "Import" in Menu options
     */
    private void onImportClicked() {
        Intent i = new Intent();
        i.setClass(this, FileImportActivity.class);
        startActivityForResult(i, REQ_CODE_IMPORT);
    }

    /**
     * Callback when user click "Clear history" in Menu options
     */
    private void onClearHistory() {
        int size = mAdapter.getCount();
        for (int i = 0; i < size; i++) {
            Object o = mAdapter.getItem(i);
            if (o instanceof HistoryFragment) {
                HistoryFragment f = (HistoryFragment) o;
                f.clearHistory();
            }
        }
    }

    private void onResultImport(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "Imported from file " + uri.getPath());
        }
    }

    /**
     * Callback when user click "License" in Menu options
     */
    private void onShowLicenses() {
        LibsBuilder builder = new LibsBuilder();
        builder.withAboutIconShown(true)
                .withAboutVersionShown(true)
                .start(this);
    }

    /**
     * Adapter for ViewPager. To use addFragment() to add items.
     */
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        final List<Fragment> iFragments = new ArrayList<>();
        final List<CharSequence> iTitles = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment f, CharSequence title) {
            iFragments.add(f);
            iTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return iFragments.get(position);
        }

        @Override
        public int getCount() {
            return iFragments.size();
        }

        @Override
        public String getPageTitle(int pos) {
            return iTitles.get(pos).toString();
        }
    }
}
