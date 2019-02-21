package org.zeroxlab.momodict

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import org.zeroxlab.momodict.ui.HistoryFragment
import org.zeroxlab.momodict.ui.InputFragment
import org.zeroxlab.momodict.ui.MemoFragment
import org.zeroxlab.momodict.widget.BackKeyHandler
import org.zeroxlab.momodict.widget.PagerFocusBroadcaster
import kotlinx.android.synthetic.main.activity_with_one_viewpager.fragment_container as mPager

/**
 * Main Activity, it consists several tabs which present by ViewPager.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: FragmentPagerAdapterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_one_viewpager)
        initView()
        // Should enable StrictMode for #13
        //StrictModeUtil.enableInDevMode()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_manage_dictionaries -> onManageClicked()
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
            val handled = mAdapter.getFragment(0).let { firstItem ->
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
        // To create fragments for Tabs, and manage them by FragmentPagerAdapterImpl
        mAdapter = FragmentPagerAdapterImpl(supportFragmentManager)

        mPager.apply {
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
    private fun onManageClicked() {
        Intent().let {
            it.setClass(this, ManageDictActivity::class.java)
            startActivityForResult(it, REQ_CODE_IMPORT)
        }
    }

    /**
     * Callback when user click "Clear history" in Menu options
     */
    private fun onClearHistory() {
        (0..(mAdapter.count - 1))
                .map { mAdapter.getFragment(it) }
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
    inner class FragmentPagerAdapterImpl internal constructor(fm: FragmentManager)
        : FragmentStatePagerAdapter(fm) {

        internal val TITLE_INPUT = "input"
        internal val TITLE_HISTORY = "history"
        internal val TITLE_MEMO = "memo"
        internal val iTitles = listOf(TITLE_INPUT, TITLE_HISTORY, TITLE_MEMO)

        internal val iFragments: SparseArray<Fragment> = SparseArray()

        override fun getItem(position: Int): Fragment {
            val title = iTitles.get(position)
            return when (title) {
                TITLE_INPUT -> InputFragment()
                TITLE_HISTORY -> HistoryFragment()
                TITLE_MEMO -> MemoFragment()
                else -> throw RuntimeException("cannot create corresponding fragment")
            }
        }

        override fun getCount(): Int = iTitles.size
        override fun getPageTitle(pos: Int): String = iTitles[pos].toString()

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val frg = super.instantiateItem(container, position) as Fragment
            iFragments.put(position, frg)
            return frg
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            iFragments.remove(position)
        }

        fun getFragment(position: Int): Fragment? {
            return iFragments.get(position)
        }
    }

    companion object {
        private val TAG = Momodict.TAG
        private val REQ_CODE_IMPORT = 0x1002
    }
}
