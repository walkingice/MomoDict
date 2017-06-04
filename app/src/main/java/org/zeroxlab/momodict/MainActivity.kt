package org.zeroxlab.momodict

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.LibsBuilder
import org.zeroxlab.momodict.ui.HistoryFragment
import org.zeroxlab.momodict.ui.InputFragment
import org.zeroxlab.momodict.ui.MemoFragment
import org.zeroxlab.momodict.widget.BackKeyHandler
import org.zeroxlab.momodict.widget.PagerFocusBroadcaster
import java.util.*

/**
 * Main Activity, it consists several tabs which present by ViewPager.
 */
class MainActivity : AppCompatActivity() {

    private var mPager: ViewPager? = null
    private lateinit var mAdapter: MyPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_one_viewpager)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, R.id.menu_import, Menu.NONE, "Import dictionary")
        menu.add(Menu.NONE, R.id.menu_clear_history, Menu.NONE, "Clear History")
        menu.add(Menu.NONE, R.id.menu_license, Menu.NONE, "License")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_import -> {
                onImportClicked()
                return true
            }
            R.id.menu_clear_history -> {
                onClearHistory()
                return true
            }
            R.id.menu_license -> {
                onShowLicenses()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent) {
        when (reqCode) {
            REQ_CODE_IMPORT -> onResultImport(resultCode, data)
        }
    }

    override fun onBackPressed() {
        // 1. if user not in first tab, move to first tab
        // 2. if the first tab can handle back key, do nothing here
        // 3. otherwise, call super.onBackPressed(usually close this activity)
        if (mPager!!.currentItem != 0) {
            mPager!!.currentItem = 0
        } else {
            val first = mAdapter.getItem(0)
            if (first is BackKeyHandler) {
                if (first.backKeyHandled()) {
                    return
                }
            }
            super.onBackPressed()
        }
    }

    private fun initView() {
        // To create fragments for Tabs, and manage them by MyPagerAdapter
        val tabs = findViewById(R.id.tabs) as TabLayout
        val mgr = supportFragmentManager
        mAdapter = MyPagerAdapter(mgr)
        mAdapter.addFragment(InputFragment(), "Input")
        mAdapter.addFragment(HistoryFragment(), "History")
        mAdapter.addFragment(MemoFragment(), "Memo")

        mPager = findViewById(R.id.fragment_container) as ViewPager
        mPager!!.adapter = mAdapter
        // To notify fragment, when page-change happens
        mPager!!.addOnPageChangeListener(PagerFocusBroadcaster(mAdapter))
        tabs.setupWithViewPager(mPager)

        //init action bar
        val toolbar = findViewById(R.id.actionbar) as Toolbar
        toolbar.setNavigationIcon(R.mipmap.ic_logo)
        setSupportActionBar(toolbar)
    }

    /**
     * Callback when user click "Import" in Menu options
     */
    private fun onImportClicked() {
        val i = Intent()
        i.setClass(this, FileImportActivity::class.java)
        startActivityForResult(i, REQ_CODE_IMPORT)
    }

    /**
     * Callback when user click "Clear history" in Menu options
     */
    private fun onClearHistory() {
        val size = mAdapter.count
        for (i in 0..size - 1) {
            val o = mAdapter.getItem(i)
            if (o is HistoryFragment) {
                o.clearHistory()
            }
        }
    }

    private fun onResultImport(resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            val uri = data.data
            Log.d(TAG, "Imported from file " + uri.path)
        }
    }

    /**
     * Callback when user click "License" in Menu options
     */
    private fun onShowLicenses() {
        val builder = LibsBuilder()
        builder.withAboutIconShown(true)
                .withAboutVersionShown(true)
                .start(this)
    }

    /**
     * Adapter for ViewPager. To use addFragment() to add items.
     */
    private inner class MyPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        internal val iFragments: MutableList<Fragment> = ArrayList()
        internal val iTitles: MutableList<CharSequence> = ArrayList()

        internal fun addFragment(f: Fragment, title: CharSequence) {
            iFragments.add(f)
            iTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return iFragments[position]
        }

        override fun getCount(): Int {
            return iFragments.size
        }

        override fun getPageTitle(pos: Int): String {
            return iTitles[pos].toString()
        }
    }

    companion object {

        private val TAG = Momodict.TAG
        private val REQ_CODE_IMPORT = 0x1002
    }
}
