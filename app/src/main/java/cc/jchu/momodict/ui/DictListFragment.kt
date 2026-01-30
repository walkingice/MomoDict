package cc.jchu.momodict.ui

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
import cc.jchu.momodict.Controller
import cc.jchu.momodict.databinding.FragmentDictionariesListBinding
import cc.jchu.momodict.model.Book
import cc.jchu.momodict.widget.BookRowPresenter
import cc.jchu.momodict.widget.SelectorAdapter
import cc.jchu.momodict.widget.SelectorAdapter.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DictListFragment : Fragment() {
    lateinit var binding: FragmentDictionariesListBinding
    lateinit var mCtrl: Controller
    lateinit var mAdapter: SelectorAdapter

    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtrl = Controller(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDictionariesListBinding.inflate(inflater)
        coroutineScope = viewLifecycleOwner.lifecycle.coroutineScope
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.btn1.setOnClickListener { onImportClicked() }

        binding.list.let {
            val decoration =
                DividerItemDecoration(
                    context,
                    (it.layoutManager as LinearLayoutManager).orientation,
                )
            it.addItemDecoration(decoration)

            val map = HashMap<Type, SelectorAdapter.Presenter<*>>()
            val listener = View.OnClickListener { v -> onRemoveBookClicked(v) }
            map[Type.A] = BookRowPresenter(listener)

            it.adapter =
                SelectorAdapter(map).also { adapter ->
                    mAdapter = adapter
                    reloadBooks()
                }
        }
    }

    private fun onRemoveBookClicked(v: View) {
        val tag: Book = v.tag as Book
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
            .setPositiveButton("Remove") { _, _ ->
                coroutineScope?.launch {
                    val success = mCtrl.removeBook(tag.bookName)
                    finishCb(success)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                // do nothing on canceling
            }
            .create()
            .show()
    }

    private fun reloadBooks() {
        mAdapter.clear()
        coroutineScope?.launch {
            val books = mCtrl.getBooks()
            books.forEach { book -> mAdapter.addItem(book, Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun onImportClicked() {
        val listener = activity as? FragmentListener ?: return
        listener.onNotified(this, FragmentListener.TYPE.VIEW_ACTION, OPEN_IMPORT_FRAGMENT)
    }

    companion object {
        const val OPEN_IMPORT_FRAGMENT = "ask_to_pick_file"

        fun newInstance(): DictListFragment {
            return DictListFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }
}
