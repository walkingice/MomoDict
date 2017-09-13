package org.zeroxlab.momodict.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.WordActivity
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.widget.HistoryRowPresenter
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.ViewPagerFocusable

import java.util.Date
import java.util.HashMap

/**
 * A fragment to display a list which contains user queried texts.
 */
class HistoryFragment : Fragment(), ViewPagerFocusable {

    private var mCtrl: Controller? = null
    private var mAdapter: SelectorAdapter? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(activity)

        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, HistoryRowPresenter(
                { view -> onRowClicked(view.tag as String) }
        ) { view ->
            onRowLongClicked(view.tag as String)
            true
        })
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_history, container, false)
        initViews(fragmentView)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        onUpdateList()
    }

    /**
     * Callback for [ViewPagerFocusable]. Called when ViewPager focused this fragment
     */
    override fun onViewPagerFocused() {
        val view = activity.currentFocus
        if (view != null) {
            // hide soft-keyboard since there is no input field in this fragment
            val imm = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        onUpdateList()
    }

    fun clearHistory() {
        mCtrl!!.clearRecords()
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

    private fun onRowClicked(keyWord: String) {
        val intent = WordActivity.createIntent(activity, keyWord)
        startActivity(intent)
    }

    private fun onRowLongClicked(keyWord: String) {
        AlertDialog.Builder(activity)
                .setTitle(keyWord)
                .setPositiveButton("Remove") { dialogInterface, i ->
                    // remove this word from history
                    mCtrl!!.removeRecord(keyWord)
                    onUpdateList()
                }
                .setNeutralButton("Memo") { dialogInterface, i ->
                    // add this word to memo
                    mCtrl!!.cards
                            .filter { card -> TextUtils.equals(keyWord, card.wordStr) }
                            .toList()
                            .subscribe { list ->
                                val card = if (list.size == 0) Card() else list[0]
                                card.wordStr = if (TextUtils.isEmpty(card.wordStr)) keyWord else card.wordStr
                                card.time = Date()
                                mCtrl!!.setCard(card)
                            }
                }
                .setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                    // do nothing on canceling
                }
                .create()
                .show()
    }

    private fun onUpdateList() {
        mAdapter!!.clear()
        mCtrl!!.records.subscribe(
                { record -> mAdapter!!.addItem(record, SelectorAdapter.Type.A) },
                { e -> e.printStackTrace() }
        ) { mAdapter!!.notifyDataSetChanged() }
    }
}
