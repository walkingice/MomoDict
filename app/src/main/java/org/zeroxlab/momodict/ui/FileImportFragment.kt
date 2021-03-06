package org.zeroxlab.momodict.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.reader.Reader
import java.io.File
import kotlinx.android.synthetic.main.fragment_file_import.btn_1 as mBtnChoose
import kotlinx.android.synthetic.main.fragment_file_import.btn_2 as mBtnImport
import kotlinx.android.synthetic.main.fragment_file_import.text_1 as mText

class FileImportFragment : Fragment() {

    private var mExists = false

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        val path = arguments?.getString(ARG_PATH)
        if (TextUtils.isEmpty(path)) {
            throw RuntimeException("No file path to import")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file_import, container, false)
    }

    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        initViews(fragmentView)
    }

    override fun onResume() {
        super.onResume()
        val dict = File(arguments?.getString(ARG_PATH)!!)
        mExists = dict.exists() && dict.isFile
        mText.text = if (mExists)
            String.format("Using file: %s", dict.name)
        else
            String.format("File %s not exists", dict.path)
        mBtnImport.isEnabled = mExists
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onRequestPermissionsResult(
        reqCode: Int,
        permissions: Array<String>,
        response: IntArray
    ) {
        if (reqCode == REQ_CODE_READ_EXTERNAL && response[0] == PackageManager.PERMISSION_GRANTED) {
            mBtnImport.isEnabled = mExists
        }
    }

    private fun checkPermission() {
        val readPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            mBtnImport.isEnabled = false
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQ_CODE_READ_EXTERNAL
            )
        }
    }

    private fun initViews(fv: View) {
        mBtnChoose.setOnClickListener { v ->
            if (activity is FragmentListener) {
                val parent = activity as FragmentListener
                parent.onNotified(this, FragmentListener.TYPE.VIEW_ACTION, PICK_A_FILE)
            }
        }

        mBtnImport.setOnClickListener { v -> onImportButtonClicked() }
    }

    private fun onImportButtonClicked() {
        mBtnImport.isEnabled = false
        mText.text = "Importing....."
        val runnable = {
            val activity = requireActivity()
            val reader = Reader(
                activity.cacheDir.path,
                arguments!!.getString(ARG_PATH)!!
            )
            reader.parse(activity)
            val intent = Intent()
            intent.data = Uri.parse(arguments!!.getString(ARG_PATH))
            activity.setResult(Activity.RESULT_OK, intent)
            activity.runOnUiThread { mText.text = "Imported" }
            activity.finish()
        }
        val t = Thread(runnable)
        t.start()
    }

    companion object {

        val ARG_PATH = "argument_path"
        val PICK_A_FILE = "to_pick_a_file_to_import"

        private val REQ_CODE_READ_EXTERNAL = 0x42

        fun newInstance(path: String): FileImportFragment {
            val fragment = FileImportFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            fragment.arguments = args
            return fragment
        }
    }
}
