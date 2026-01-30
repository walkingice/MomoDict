package cc.jchu.momodict.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.Controller
import cc.jchu.momodict.R
import cc.jchu.momodict.WordActivity
import cc.jchu.momodict.widget.CardRowPresenter
import cc.jchu.momodict.widget.SelectorAdapter
import cc.jchu.momodict.widget.ViewPagerFocusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A fragment to display a list of texts as user's memo.
 */
class MemoFragment : Fragment(), ViewPagerFocusable {
    private lateinit var mCtrl: Controller
    private lateinit var mAdapter: SelectorAdapter

    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(requireActivity())

        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter>()
        map.put(
            SelectorAdapter.Type.A,
            CardRowPresenter(
                { view -> view.tag?.let { onRowClicked(view.tag as String) } },
                { view -> view.tag?.let { onRowLongClicked(view.tag as String) } },
            ),
        )
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?,
    ): View? {
        coroutineScope = viewLifecycleOwner.lifecycle.coroutineScope
        val fragmentView = inflater.inflate(R.layout.fragment_memo, container, false)
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

    override fun onViewPagerFocused() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm =
                requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        onUpdateList()
    }

    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as RecyclerView
        val mgr = list.layoutManager as LinearLayoutManager
        val decoration =
            DividerItemDecoration(
                list.context,
                mgr.orientation,
            )
        list.addItemDecoration(decoration)
        list.adapter = mAdapter
    }

    /**
     * Callback when user click a long
     */
    private fun onRowClicked(keyWord: String) {
        val intent = WordActivity.createIntent(requireActivity(), keyWord)
        startActivity(intent)
    }

    /**
     * Callback when user long-click a long
     */
    private fun onRowLongClicked(keyWord: String) {
        AlertDialog.Builder(requireActivity())
            .setTitle(keyWord)
            .setPositiveButton("Remove") { _, _ ->
                // remove this word from memo
                coroutineScope?.launch {
                    mCtrl.removeCards(keyWord)
                    onUpdateList()
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
            val cards = mCtrl.getCards()
            cards.forEach { card -> mAdapter.addItem(card, SelectorAdapter.Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }
}
