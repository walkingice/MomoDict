package cc.jchu.momodict

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cc.jchu.momodict.ui.DictListFragment
import cc.jchu.momodict.ui.FileImportFragment
import cc.jchu.momodict.ui.FilePickerFragment
import cc.jchu.momodict.ui.FragmentListener

class ManageDictActivity : AppCompatActivity(), FragmentListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_one_fragment)
        val toolbar = findViewById(R.id.actionbar) as Toolbar
        setSupportActionBar(toolbar)
        setFragments()
    }

    override fun onBackPressed() {
        val fragPick = supportFragmentManager.findFragmentByTag(TAG_PICK_FILE)
        if (fragPick != null && fragPick.isVisible) {
            // In FilePickerFragment, Back-key can go to parent directory
            val handled = (fragPick as FilePickerFragment).goParentDirectory()
            if (handled) {
                return
            }
        }

        updatePathData()

        // really pop Fragment
        super.onBackPressed()
    }

    private fun popFragment() {
        updatePathData()
        // simulate back key to pop fragment
        super.onBackPressed()
    }

    private fun updatePathData() {
        val fragPick = supportFragmentManager.findFragmentByTag(TAG_PICK_FILE)
        val fragImport = supportFragmentManager.findFragmentByTag(TAG_IMPORT_FILE)
        if (fragPick != null && fragImport != null) {
            val chosen = fragPick.arguments?.getString(FilePickerFragment.ARG_PATH)
            if (chosen != null && chosen.isNotEmpty()) {
                fragImport.arguments?.putString(FileImportFragment.ARG_PATH, chosen)
            }
        }
    }

    private fun setFragments() {
        val frg = DictListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, frg, TAG_DICT_LIST)
            .commit()
    }

    override fun onNotified(
        from: androidx.fragment.app.Fragment?,
        type: FragmentListener.TYPE,
        payload: Any?,
    ) {
        when (type) {
            FragmentListener.TYPE.POP_FRAGMENT -> popFragment()
            FragmentListener.TYPE.VIEW_ACTION -> {
                if (from != null && payload != null) {
                    handleViewAction(from, payload)
                }
            }

            FragmentListener.TYPE.START_ACTIVITY -> Unit
            FragmentListener.TYPE.UPDATE_TITLE -> Unit
        }
    }

    private fun handleViewAction(
        from: androidx.fragment.app.Fragment,
        payload: Any,
    ) {
        when (payload) {
            DictListFragment.OPEN_IMPORT_FRAGMENT ->
                replaceFragment(
                    FileImportFragment.newInstance(sEXT_DIR),
                    TAG_IMPORT_FILE,
                )

            FileImportFragment.PICK_A_FILE -> onPickFileClicked()
        }
    }

    private fun onPickFileClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            requestManageExternalStoragePermission()
        } else {
            val pickerFragment = FilePickerFragment.newInstance(sEXT_DIR, sEXT)
            replaceFragment(pickerFragment, TAG_PICK_FILE)
        }
    }

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun replaceFragment(
        frag: androidx.fragment.app.Fragment,
        tag: String,
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, frag, tag)
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private val sEXT_DIR = Environment.getExternalStorageDirectory().path
        private val sEXT = ".tar.bz2" // supported file extension
        private val TAG_DICT_LIST = "fragment_to_list_dictionaries"
        private val TAG_IMPORT_FILE = "fragment_to_import_file"
        private val TAG_PICK_FILE = "fragment_to_pick_file"
    }
}
