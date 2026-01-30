package cc.jchu.momodict.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cc.jchu.momodict.databinding.FragmentFilePickerBinding
import cc.jchu.momodict.widget.FileRowPresenter
import cc.jchu.momodict.widget.SelectorAdapter
import java.io.File

class FilePickerFragment : Fragment() {
    private lateinit var binding: FragmentFilePickerBinding
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter>()
        map[SelectorAdapter.Type.A] = FileRowPresenter(requireActivity()) { v -> onFileClicked(v) }
        mAdapter = SelectorAdapter(map)
        binding = FragmentFilePickerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        fragmentView: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(fragmentView, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        updateList(mCurrentPath!!)
    }

    private fun initViews() {
        val mgr = binding.list.layoutManager as LinearLayoutManager
        binding.list.addItemDecoration(DividerItemDecoration(context, mgr.orientation))

        binding.pickerBtnChoose.isEnabled = false
        binding.pickerBtnChoose.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments?.putString(ARG_PATH, mChosen)
                (activity as FragmentListener).onNotified(
                    this,
                    FragmentListener.TYPE.POP_FRAGMENT,
                    null,
                )
            }
        }

        binding.pickerBtnCancel.setOnClickListener { view ->
            if (activity is FragmentListener) {
                arguments?.remove(ARG_PATH)
                (activity as FragmentListener).onNotified(
                    this,
                    FragmentListener.TYPE.POP_FRAGMENT,
                    null,
                )
            }
        }
        binding.list.adapter = mAdapter
    }

    private fun updateList(path: String) {
        binding.pickerCurrentPath.text = mCurrentPath
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

        binding.pickerBtnChoose.isEnabled = mChosen != null &&
            mChosen!!.endsWith(mExtension!!) &&
            File(mChosen!!).canRead()
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
            extension: String,
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
