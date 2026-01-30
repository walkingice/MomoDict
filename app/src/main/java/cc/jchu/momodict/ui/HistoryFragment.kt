package cc.jchu.momodict.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.Controller
import cc.jchu.momodict.R
import cc.jchu.momodict.WordActivity
import cc.jchu.momodict.model.Card
import cc.jchu.momodict.widget.HistoryRowPresenter
import cc.jchu.momodict.widget.SelectorAdapter
import cc.jchu.momodict.widget.ViewPagerFocusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.HashMap

/**
 * A fragment to display a list which contains user queried texts.
 */
class HistoryFragment : androidx.fragment.app.Fragment(), ViewPagerFocusable {
    private lateinit var mCtrl: Controller
    private lateinit var mAdapter: SelectorAdapter

    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(requireActivity())

        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(
            SelectorAdapter.Type.A,
            HistoryRowPresenter(
                { view -> onRowClicked(view.tag as String) },
            ) { view ->
                onRowLongClicked(view.tag as String)
                true
            },
        )
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?,
    ): View? {
        coroutineScope = viewLifecycleOwner.lifecycle.coroutineScope
        val fragmentView = inflater.inflate(R.layout.fragment_history, container, false)
        initViews(fragmentView)
        return fragmentView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope = null
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
            val imm =
                requireActivity()
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
        val list = fv.findViewById<RecyclerView>(R.id.list)
        val mgr = list.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val decoration =
            androidx.recyclerview.widget.DividerItemDecoration(
                list.context,
                mgr.orientation,
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
            .setPositiveButton("Remove") { _, _ ->
                // remove this word from history
                coroutineScope?.launch {
                    mCtrl.removeRecord(keyWord)
                    onUpdateList()
                }
            }
            .setNeutralButton("Memo") { _, _ ->
                // add this word to memo
                coroutineScope?.launch {
                    val cards = mCtrl.getCards()
                    val list = cards.filter { card -> TextUtils.equals(keyWord, card.wordStr) }
                    val card = if (list.isEmpty()) Card(keyWord) else list[0]
                    card.wordStr =
                        if (TextUtils.isEmpty(card.wordStr)) {
                            keyWord
                        } else {
                            card.wordStr
                        }
                    card.time = Date()
                    mCtrl.setCard(card)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                // do nothing on canceling
            }
            .create()
            .show()
    }

    private fun onUpdateList() {
        mAdapter.clear()
        coroutineScope?.launch {
            mCtrl.getRecords()
                .forEach { record -> mAdapter.addItem(record, SelectorAdapter.Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }
}
