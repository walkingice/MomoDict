package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.widget.FileRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter

import java.io.File
import java.util.HashMap

class FilePickerFragment : Fragment() {
    private var mList: RecyclerView? = null
    private var mAdapter: SelectorAdapter? = null
    private var mBtnChoose: Button? = null
    private var mBtnCancel: Button? = null
    private var mCurrentPathView: TextView? = null
    private var mChosen: String? = null
    private var mCurrentPath: String? = null
    private var mExtension: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        mCurrentPath = args.getString(ARG_PATH)
        mExtension = args.getString(ARG_EXTENSION, "")

        // check
        val check = File(mCurrentPath!!)
        if (!check.exists() && !check.canRead()) {
            throw RuntimeException("Cannot open path:" + mCurrentPath!!)
        }
        if (check.isFile) {
            mCurrentPath = check.parentFile.path
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_file_picker, container, false)
        initViews(view)
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, FileRowPresenter(context) { v -> onFileClicked(v) })
        mAdapter = SelectorAdapter(map)
        mList!!.adapter = mAdapter
        return view
    }

    override fun onResume() {
        super.onResume()
        updateList(mCurrentPath!!)
    }

    private fun initViews(container: View) {
        mList = container.findViewById(R.id.list) as RecyclerView
        val mgr = mList!!.layoutManager as LinearLayoutManager
        mList!!.addItemDecoration(DividerItemDecoration(context, mgr.orientation))
        mCurrentPathView = container.findViewById(R.id.picker_current_path) as TextView
        mBtnCancel = container.findViewById(R.id.picker_btn_cancel) as Button
        mBtnChoose = container.findViewById(R.id.picker_btn_choose) as Button
        mBtnChoose!!.isEnabled = false

        mBtnChoose!!.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments.putString(ARG_PATH, mChosen)
                (activity as FragmentListener).onNotified(this,
                        FragmentListener.TYPE.POP_FRAGMENT, null)
            }
        }
        mBtnCancel!!.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments.remove(ARG_PATH)
                (activity as FragmentListener).onNotified(this,
                        FragmentListener.TYPE.POP_FRAGMENT, null)
            }
        }
    }

    private fun updateList(path: String) {
        mCurrentPathView!!.text = mCurrentPath
        mAdapter!!.clear()
        val f = File(path)
        if (f.parentFile != null) {
            val parent = FileRowPresenter.Item("..", f.parentFile)
            mAdapter!!.addItem(parent, SelectorAdapter.Type.A)
        }
        val dir = if (f.isDirectory) f else f.parentFile
        val files = dir.listFiles()
        if (files != null) {
            for (file in dir.listFiles()) {
                val item = FileRowPresenter.Item(file.name, file)
                mAdapter!!.addItem(item, SelectorAdapter.Type.A)
            }
        }

        mBtnChoose!!.isEnabled = mChosen != null
                && mChosen!!.endsWith(mExtension!!)
                && File(mChosen!!).canRead()
        mAdapter!!.notifyDataSetChanged()
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

        fun newInstance(path: String,
                        extension: String): FilePickerFragment {
            val fragment = FilePickerFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            args.putString(ARG_EXTENSION, extension)
            fragment.arguments = args
            return fragment
        }
    }
}
