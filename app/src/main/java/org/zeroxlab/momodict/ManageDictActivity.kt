package org.zeroxlab.momodict

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import org.zeroxlab.momodict.ui.DictListFragment
import org.zeroxlab.momodict.ui.FileImportFragment
import org.zeroxlab.momodict.ui.FilePickerFragment
import org.zeroxlab.momodict.ui.FragmentListener

class ManageDictActivity : AppCompatActivity(), FragmentListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_one_fragment)
        val toolbar = findViewById(R.id.actionbar) as Toolbar
        setSupportActionBar(toolbar)
        setFragments()
    }

    private fun setFragments() {
        val frg = DictListFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, frg, TAG_DICT_LIST)
                .commit()
    }

    override fun onNotified(from: Fragment?, type: FragmentListener.TYPE, payload: Any?) {
        when (type) {
            FragmentListener.TYPE.POP_FRAGMENT -> popFragment()
            FragmentListener.TYPE.VIEW_ACTION -> {
                if (from != null && payload != null) {
                    handleViewAction(from, payload)
                }
            }
        }
    }

    private fun handleViewAction(from: Fragment, payload: Any) {
        when (payload) {
            DictListFragment.OPEN_IMPORT_FRAGMENT -> openImportFragment()
            FileImportFragment.PICK_A_FILE -> openFilePicker()
        }
    }

    private fun openImportFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,
                        FileImportFragment.newInstance(sEXT_DIR),
                        TAG_IMPORT_FILE)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
    }

    private fun openFilePicker() {
        val mgr = supportFragmentManager
        mgr.beginTransaction()
                .replace(R.id.fragment_container,
                        FilePickerFragment.newInstance(sEXT_DIR, sEXT),
                        TAG_PICK_FILE)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentByTag(TAG_PICK_FILE)
        if (f == null) {
            popFragment()
        } else {
            val handled = (f as FilePickerFragment).goParentDirectory()
            if (!handled) {
                popFragment()
            }
        }
    }

    private fun popFragment() {
        // FIXME: so stupid implementation
        val fragPick = supportFragmentManager.findFragmentByTag(TAG_PICK_FILE)
        val fragImport = supportFragmentManager.findFragmentByTag(TAG_IMPORT_FILE)
        if (fragPick != null && fragImport != null) {
            val chosen = fragPick.arguments.getString(FilePickerFragment.ARG_PATH)
            if (!TextUtils.isEmpty(chosen)) {
                fragImport.arguments.putString(FileImportFragment.ARG_PATH, chosen)
            }
        }

        super.onBackPressed()
    }

    companion object {
        private val sEXT_DIR = Environment.getExternalStorageDirectory().path
        private val sEXT = ".tar.bz2" // supported file extension
        private val TAG_DICT_LIST = "fragment_to_list_dictionaries"
        private val TAG_IMPORT_FILE = "fragment_to_import_file"
        private val TAG_PICK_FILE = "fragment_to_pick_file"
    }
}
