package org.zeroxlab.momodict.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
class HistoryFragment : androidx.fragment.app.Fragment(), ViewPagerFocusable {

    private lateinit var mCtrl: Controller
    private lateinit var mAdapter: SelectorAdapter

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(requireActivity())

        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, HistoryRowPresenter(
            { view -> onRowClicked(view.tag as String) }
        ) { view ->
            onRowLongClicked(view.tag as String)
            true
        })
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_history, container, false)
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
        val view = activity?.currentFocus
        if (view != null) {
            // hide soft-keyboard since there is no input field in this fragment
            val imm = requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        onUpdateList()
    }

    fun clearHistory() {
        // even if Fragment is closed, this coroutine should still finish its job
        GlobalScope.launch {
            mCtrl.clearRecords()
        }
        onUpdateList()
    }

    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as androidx.recyclerview.widget.RecyclerView
        val mgr = list.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val decoration = androidx.recyclerview.widget.DividerItemDecoration(
            list.context,
            mgr.orientation
        )
        list.addItemDecoration(decoration)
        list.adapter = mAdapter
    }

    private fun onRowClicked(keyWord: String) {
        val intent = WordActivity.createIntent(requireActivity(), keyWord)
        startActivity(intent)
    }

    private fun onRowLongClicked(keyWord: String) {
        AlertDialog.Builder(requireActivity())
            .setTitle(keyWord)
            .setPositiveButton("Remove") { dialogInterface, i ->
                // remove this word from history
                requireActivity().lifecycle.coroutineScope.launch {
                    mCtrl.removeRecord(keyWord)
                    onUpdateList()
                }
            }
            .setNeutralButton("Memo") { dialogInterface, i ->
                // add this word to memo
                requireActivity().lifecycle.coroutineScope.launch {
                    val cards = mCtrl.getCards()
                    val list = cards.filter { card -> TextUtils.equals(keyWord, card.wordStr) }
                    val card = if (list.isEmpty()) Card(keyWord) else list[0]
                    card.wordStr =
                        if (TextUtils.isEmpty(card.wordStr)) keyWord
                        else card.wordStr
                    card.time = Date()
                    mCtrl.setCard(card)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                // do nothing on canceling
            }
            .create()
            .show()
    }

    private fun onUpdateList() {
        mAdapter.clear()
        requireActivity().lifecycle.coroutineScope.launch {
            mCtrl.getRecords()
                .forEach { record -> mAdapter.addItem(record, SelectorAdapter.Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }
}
