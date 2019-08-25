package org.zeroxlab.momodict.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.widget.FileRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter
import java.io.File
import java.util.HashMap
import kotlinx.android.synthetic.main.fragment_file_picker.list as mList
import kotlinx.android.synthetic.main.fragment_file_picker.picker_btn_cancel as mBtnCancel
import kotlinx.android.synthetic.main.fragment_file_picker.picker_btn_choose as mBtnChoose
import kotlinx.android.synthetic.main.fragment_file_picker.picker_current_path as mCurrentPathView

class FilePickerFragment : Fragment() {
    private lateinit var mAdapter: SelectorAdapter
    private var mChosen: String? = null
    private var mCurrentPath: String? = null
    private var mExtension: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        mCurrentPath = args?.getString(ARG_PATH)
        mExtension = args?.getString(ARG_EXTENSION, "")

        // check
        val check = File(mCurrentPath!!)
        if (!check.exists() && !check.canRead()) {
            throw RuntimeException("Cannot open path:" + mCurrentPath!!)
        }
        if (check.isFile) {
            mCurrentPath = check.parentFile.path
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map[SelectorAdapter.Type.A] = FileRowPresenter(requireActivity()) { v -> onFileClicked(v) }
        mAdapter = SelectorAdapter(map)
        return inflater.inflate(R.layout.fragment_file_picker, container, false)
    }

    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        updateList(mCurrentPath!!)
    }

    private fun initViews() {
        val mgr = mList.layoutManager as LinearLayoutManager
        mList.addItemDecoration(DividerItemDecoration(context, mgr.orientation))

        mBtnChoose.isEnabled = false
        mBtnChoose.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments?.putString(ARG_PATH, mChosen)
                (activity as FragmentListener).onNotified(
                    this,
                    FragmentListener.TYPE.POP_FRAGMENT, null
                )
            }
        }

        mBtnCancel.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments?.remove(ARG_PATH)
                (activity as FragmentListener).onNotified(
                    this,
                    FragmentListener.TYPE.POP_FRAGMENT, null
                )
            }
        }
        mList.adapter = mAdapter
    }

    private fun updateList(path: String) {
        mCurrentPathView.text = mCurrentPath
        mAdapter.clear()
        val f = File(path)
        if (f.parentFile != null) {
            val parent = FileRowPresenter.Item("..", f.parentFile)
            mAdapter.addItem(parent, SelectorAdapter.Type.A)
        }
        val dir = if (f.isDirectory) f else f.parentFile
        val files = dir.listFiles()
        if (files != null) {
            for (file in dir.listFiles()) {
                val item = FileRowPresenter.Item(file.name, file)
                mAdapter.addItem(item, SelectorAdapter.Type.A)
            }
        }

        mBtnChoose.isEnabled = mChosen != null
                && mChosen!!.endsWith(mExtension!!)
                && File(mChosen!!).canRead()
        mAdapter.notifyDataSetChanged()
    }

    private fun onFileClicked(v: View) {
        val file = v.tag as File
        val selectedPath = file.path
        if (TextUtils.equals(mChosen, file.path)) {
            return
        }
        if (file.isFile && selectedPath.endsWith(mExtension!!)) {
            mChosen = selectedPath
        } else {
            mCurrentPath = selectedPath
        }
        updateList(mCurrentPath!!)
    }

    fun goParentDirectory(): Boolean {
        val current = File(mCurrentPath!!)
        val parent = current.parentFile
        if (parent == null) {
            return false
        } else {
            mCurrentPath = parent.path
            updateList(mCurrentPath!!)
            return true
        }
    }

    companion object {

        val ARG_PATH = "path_to_open"
        val ARG_EXTENSION = "filename_extension"

        fun newInstance(
            path: String,
            extension: String
        ): FilePickerFragment {
            val fragment = FilePickerFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            args.putString(ARG_EXTENSION, extension)
            fragment.arguments = args
            return fragment
        }
    }
}
