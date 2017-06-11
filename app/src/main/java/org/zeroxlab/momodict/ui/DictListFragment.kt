package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.widget.DictionaryRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.SelectorAdapter.Type

class DictListFragment : Fragment() {

    lateinit var mCtrl: Controller
    lateinit var mBtnImport: View
    lateinit var mList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtrl = Controller(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val frgView = inflater!!.inflate(R.layout.fragment_dictionaries_list, container, false)
        mBtnImport = frgView.findViewById(R.id.btn_1).also {
            it.setOnClickListener({ onImportClicked() })
        }

        mList = (frgView.findViewById(R.id.list) as RecyclerView).also {
            val decoration = DividerItemDecoration(context,
                    (it.layoutManager as LinearLayoutManager).orientation)
            it.addItemDecoration(decoration)

            val map = HashMap<Type, SelectorAdapter.Presenter<*>>()
            map.put(SelectorAdapter.Type.A, DictionaryRowPresenter())

            it.adapter = SelectorAdapter(map).also { adapter ->
                mCtrl.books.forEach { book -> adapter.addItem(book.bookName, Type.A) }
            }
        }
        return frgView
    }

    private fun onImportClicked() {
        if (activity is FragmentListener) {
            (activity as FragmentListener).onNotified(this,
                    FragmentListener.TYPE.VIEW_ACTION,
                    OPEN_IMPORT_FRAGMENT)
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
