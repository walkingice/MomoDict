package org.zeroxlab.momodict.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.WordActivity
import org.zeroxlab.momodict.widget.CardRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.ViewPagerFocusable
import java.util.*

/**
 * A fragment to display a list of texts as user's memo.
 */
class MemoFragment : Fragment(), ViewPagerFocusable {

    private var mCtrl: Controller? = null
    private var mAdapter: SelectorAdapter? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(activity)

        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, CardRowPresenter(
                { view -> onRowClicked(view.tag as String) }
        ) { view ->
            onRowLongClicked(view.tag as String)
            true
        })
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_memo, container, false)
        initViews(fragmentView)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        onUpdateList()
    }

    override fun onViewPagerFocused() {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        onUpdateList()
    }


    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as RecyclerView
        val mgr = list.layoutManager as LinearLayoutManager
        val decoration = DividerItemDecoration(list.context,
                mgr.orientation)
        list.addItemDecoration(decoration)
        list.adapter = mAdapter
    }

    /**
     * Callback when user click a long
     */
    private fun onRowClicked(keyWord: String) {
        val intent = WordActivity.createIntent(activity, keyWord)
        startActivity(intent)
    }

    /**
     * Callback when user long-click a long
     */
    private fun onRowLongClicked(keyWord: String) {
        AlertDialog.Builder(activity)
                .setTitle(keyWord)
                .setPositiveButton("Remove") { dialogInterface, i ->
                    // remove this word from memo
                    mCtrl!!.removeCards(keyWord)
                    onUpdateList()
                }
                .setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                    // do nothing on canceling
                }
                .create()
                .show()
    }

    private fun onUpdateList() {
        mAdapter!!.clear()
        mCtrl!!.cards.subscribe(
                { card -> mAdapter!!.addItem(card, SelectorAdapter.Type.A) },
                { e -> e.printStackTrace() }
        ) { mAdapter!!.notifyDataSetChanged() }
    }
}
