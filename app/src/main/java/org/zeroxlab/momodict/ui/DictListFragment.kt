package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.widget.BookRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.SelectorAdapter.Type
import kotlinx.android.synthetic.main.fragment_dictionaries_list.btn_1 as mBtnImport
import kotlinx.android.synthetic.main.fragment_dictionaries_list.list as mList

class DictListFragment : Fragment() {

    lateinit var mCtrl: Controller
    lateinit var mAdapter: SelectorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtrl = Controller(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionaries_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBtnImport.let {
            it.setOnClickListener({ onImportClicked() })
        }

        mList.let {
            val decoration = DividerItemDecoration(
                context,
                (it.layoutManager as LinearLayoutManager).orientation
            )
            it.addItemDecoration(decoration)

            val map = HashMap<Type, SelectorAdapter.Presenter<*>>()
            val listener = View.OnClickListener { v -> onRemoveBookClicked(v) }
            map.put(SelectorAdapter.Type.A, BookRowPresenter(listener))

            it.adapter = SelectorAdapter(map).also { adapter ->
                mAdapter = adapter
                reloadBooks()
            }
        }
    }

    private fun onRemoveBookClicked(v: View) {
        var tag: Book = v.tag as Book
        val finishCb = fun(success: Boolean) {
            if (success) {
                reloadBooks()
            } else {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        AlertDialog.Builder(requireActivity())
            .setTitle("Remove")
            .setMessage("To remove ${tag.bookName} ?")
            .setPositiveButton("Remove") { dialogInterface, i ->
                finishCb(mCtrl.removeBook(tag.bookName!!))
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                // do nothing on canceling
            }
            .create()
            .show()
    }

    private fun reloadBooks() {
        mAdapter.clear()
        mCtrl.getBooks(this.lifecycle.coroutineScope) {
            it.forEach { book -> mAdapter.addItem(book, Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun onImportClicked() {
        if (activity is FragmentListener) {
            (activity as FragmentListener).onNotified(
                this,
                FragmentListener.TYPE.VIEW_ACTION,
                OPEN_IMPORT_FRAGMENT
            )
        }
    }

    companion object {
        val OPEN_IMPORT_FRAGMENT = "ask_to_pick_file"
        fun newInstance(): DictListFragment {
            return DictListFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }
}
