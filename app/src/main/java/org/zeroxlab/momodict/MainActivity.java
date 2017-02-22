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

import org.zeroxlab.momodict.ui.HistoryFragment;
import org.zeroxlab.momodict.ui.InputSearchFragment;
import org.zeroxlab.momodict.ui.MemoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = Momodict.TAG;

    static final int REQ_CODE_IMPORT = 0x1002;

    private TabLayout mTabs;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_viewpager);
        initView();
        setFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.menu_import, Menu.NONE, "Import dictionary");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import:
                onImportClicked();
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
        if (mPager.getCurrentItem() != 0) {
            // not in first page
            mPager.setCurrentItem(0);
        } else {
            // FIXME: ugly
            InputSearchFragment first = (InputSearchFragment) mAdapter.getItem(0);
            if (!first.handledBackKey()) {
                super.onBackPressed();
            }
        }
    }

    private void setFragments() {
        final FragmentManager mgr = getSupportFragmentManager();
        mAdapter = new MyPagerAdapter(mgr);
        mPager.setAdapter(mAdapter);
    }

    private void initView() {
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.fragment_container);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                notifyListener(position);
            }

            @Override
            public void onPageSelected(int position) {
                notifyListener(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            private void notifyListener(int pos) {
                Object item = mAdapter.getItem(pos);
                if (item instanceof ViewPagerFocusable) {
                    ((ViewPagerFocusable) item).onViewPagerFocused();
                }
            }
        });
        mTabs.setupWithViewPager(mPager);
        initActionBar();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        toolbar.setNavigationIcon(R.mipmap.ic_logo);
        setSupportActionBar(toolbar);
    }

    private void onImportClicked() {
        Intent i = new Intent();
        i.setClass(this, FileImportActivity.class);
        startActivityForResult(i, REQ_CODE_IMPORT);
    }

    private void onResultImport(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "Imported from file " + uri.getPath());
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        final List<Fragment> iFragments = new ArrayList<>();
        final List<String> iTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            iFragments.add(new InputSearchFragment());
            iTitles.add("Main");
            iFragments.add(new HistoryFragment());
            iTitles.add("History");
            iFragments.add(new MemoFragment());
            iTitles.add("Memo");
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
            return iTitles.get(pos);
        }
    }

    public interface ViewPagerFocusable {
        void onViewPagerFocused();
    }
}
