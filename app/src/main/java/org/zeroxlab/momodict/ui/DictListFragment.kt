package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zeroxlab.momodict.R

class DictListFragment : Fragment() {

    lateinit var mBtnImport: View

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val frgView = inflater!!.inflate(R.layout.fragment_dictionaries_list, container, false)
        mBtnImport = frgView.findViewById(R.id.btn_1).also {
            it.setOnClickListener({ onImportClicked() })
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
