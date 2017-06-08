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

    private lateinit var mPager: ViewPager
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
            R.id.menu_import -> onImportClicked()
            R.id.menu_clear_history -> onClearHistory()
            R.id.menu_license -> onShowLicenses()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        when (reqCode) {
            REQ_CODE_IMPORT -> onResultImport(resultCode, data)
        }
    }

    override fun onBackPressed() {
        // 1. if user not in first tab, move to first tab
        // 2. if the first tab can handle back key, do nothing here
        // 3. otherwise, call super.onBackPressed(usually close this activity)
        if (mPager.currentItem != 0) {
            mPager.currentItem = 0
        } else {
            val handled = mAdapter.getItem(0).let { firstItem ->
                when (firstItem) {
                    is BackKeyHandler -> firstItem.backKeyHandled()
                    else -> false
                }
            }

            if (!handled) {
                super.onBackPressed()
            }
        }
    }

    private fun initView() {
        // To create fragments for Tabs, and manage them by MyPagerAdapter
        mAdapter = MyPagerAdapter(supportFragmentManager).apply {
            addFragment(InputFragment(), "Input")
            addFragment(HistoryFragment(), "History")
            addFragment(MemoFragment(), "Memo")
        }

        mPager = (findViewById(R.id.fragment_container) as ViewPager).apply {
            adapter = mAdapter
            // To notify fragment, when page-change happens
            addOnPageChangeListener(PagerFocusBroadcaster(mAdapter))
        }

        with(findViewById(R.id.tabs) as TabLayout) {
            setupWithViewPager(mPager)
        }

        //init action bar
        (findViewById(R.id.actionbar) as Toolbar).let {
            it.setNavigationIcon(R.mipmap.ic_logo)
            setSupportActionBar(it)
        }
    }

    /**
     * Callback when user click "Import" in Menu options
     */
    private fun onImportClicked() {
        Intent().let {
            it.setClass(this, FileImportActivity::class.java)
            startActivityForResult(it, REQ_CODE_IMPORT)
        }
    }

    /**
     * Callback when user click "Clear history" in Menu options
     */
    private fun onClearHistory() {
        (0..(mAdapter.count - 1))
                .map { mAdapter.getItem(it) }
                .filterIsInstance<HistoryFragment>()
                .forEach { it.clearHistory() }
    }

    private fun onResultImport(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> Log.d(TAG, "Imported from file " + data?.data?.path)
        }
    }

    /**
     * Callback when user click "License" in Menu options
     */
    private fun onShowLicenses() {
        LibsBuilder()
                .withAboutIconShown(true)
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

        override fun getItem(position: Int): Fragment = iFragments[position]
        override fun getCount(): Int = iFragments.size
        override fun getPageTitle(pos: Int): String = iTitles[pos].toString()
    }

    companion object {
        private val TAG = Momodict.TAG
        private val REQ_CODE_IMPORT = 0x1002
    }
}
