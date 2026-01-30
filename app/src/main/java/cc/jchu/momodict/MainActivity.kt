package cc.jchu.momodict

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cc.jchu.momodict.databinding.ActivityWithOneViewpagerBinding
import cc.jchu.momodict.input.InputFragment
import cc.jchu.momodict.ui.HistoryFragment
import cc.jchu.momodict.ui.MemoFragment
import cc.jchu.momodict.widget.BackKeyHandler
import cc.jchu.momodict.widget.PagerFocusBroadcaster
import com.mikepenz.aboutlibraries.LibsBuilder

/**
 * Main Activity, it consists several tabs which present by ViewPager.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mAdapter: FragmentPagerAdapterImpl

    private lateinit var mPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWithOneViewpagerBinding.inflate(layoutInflater)
        initView(binding)
        setContentView(binding.root)
        // Should enable StrictMode for #13
        // StrictModeUtil.enableInDevMode()
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

    public override fun onActivityResult(
        reqCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(reqCode, resultCode, data)
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
            val handled =
                mAdapter.getFragment(0).let { firstItem ->
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

    private fun initView(binding: ActivityWithOneViewpagerBinding) {
        mPager = binding.fragmentContainer
        // To create fragments for Tabs, and manage them by FragmentPagerAdapterImpl
        mAdapter = FragmentPagerAdapterImpl(supportFragmentManager)

        mPager.apply {
            adapter = mAdapter
            // To notify fragment, when page-change happens
            addOnPageChangeListener(PagerFocusBroadcaster(mAdapter))
        }

        with(binding.tabs) {
            setupWithViewPager(mPager)
        }

        // init action bar
        (binding.actionbar).let {
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

    private fun onResultImport(
        resultCode: Int,
        data: Intent?,
    ) {
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
    inner class FragmentPagerAdapterImpl internal constructor(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
            internal val titleInput = "input"
            internal val titleHistory = "history"
            internal val titleMemo = "memo"
            internal val titles = listOf(titleInput, titleHistory, titleMemo)

            internal val iFragments: SparseArray<Fragment> = SparseArray()

            override fun getItem(position: Int): Fragment {
                val title = titles.get(position)
                return when (title) {
                    titleInput -> InputFragment()
                    titleHistory -> HistoryFragment()
                    titleMemo -> MemoFragment()
                    else -> throw RuntimeException("cannot create corresponding fragment")
                }
            }

            override fun getCount(): Int = titles.size

            override fun getPageTitle(pos: Int): String = titles[pos].toString()

            override fun instantiateItem(
                container: ViewGroup,
                position: Int,
            ): Any {
                val frg = super.instantiateItem(container, position) as Fragment
                iFragments.put(position, frg)
                return frg
            }

            override fun destroyItem(
                container: ViewGroup,
                position: Int,
                `object`: Any,
            ) {
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
