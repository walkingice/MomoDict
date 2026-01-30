package cc.jchu.momodict.input

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.R
import cc.jchu.momodict.WordActivity
import cc.jchu.momodict.databinding.FragmentInputBinding
import cc.jchu.momodict.model.Entry
import cc.jchu.momodict.widget.BackKeyHandler
import cc.jchu.momodict.widget.SelectorAdapter
import cc.jchu.momodict.widget.ViewPagerFocusable
import cc.jchu.momodict.widget.WordRowPresenter

/**
 * Fragment to provide UI which user can input a text to query, and display a list for queried text.
 */
class InputFragment :
    Fragment(),
    BackKeyHandler,
    InputContract.View,
    ViewPagerFocusable {
    private lateinit var presenter: InputPresenter
    private lateinit var adapter: SelectorAdapter

    private lateinit var binding: FragmentInputBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        presenter = InputPresenter(requireActivity(), this)

        // create adapter for RecyclerView. Adapter handles two kinds of row, one for "dictionary"
        // and another for "word".
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(
            SelectorAdapter.Type.A,
            WordRowPresenter { view -> onRowClicked(view.tag as String) },
        )
        adapter = SelectorAdapter(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?,
    ): View? {
        binding = FragmentInputBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        fragmentView: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(fragmentView, savedInstanceState)
        initViews(fragmentView)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    /**
     * Callback for [BackKeyHandler]. Called when user clicked back key.
     * If the input field is not empty, back-key-event will clear the field.
     * We regards this as "handled"

     * @return true if this fragment handled back-key-event
     */
    override fun backKeyHandled(): Boolean {
        if (TextUtils.isEmpty(binding.input1.text)) {
            return false
        } else {
            clearInput()
            return true
        }
    }

    /**
     * Callback for [ViewPagerFocusable]. Called when ViewPager focused this fragment
     */
    override fun onViewPagerFocused() {
        // if this page becomes visible, show soft-keyboard
        binding.input1.requestFocus()
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onEnableInput(enabled: Boolean) {
        binding.input1.isEnabled = enabled
    }

    override fun onUpdateList(entries: List<Entry>) {
        if (entries.isEmpty()) {
            // User haven't input anything, just clear the list
            adapter.clear()
            adapter.notifyDataSetChanged()
        } else {
            adapter.clear()
            for (entry in entries) {
                adapter.addItem(entry.wordStr, SelectorAdapter.Type.A)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.show()
        } else {
            binding.loading.hide()
        }
    }

    override fun inputSelectAll() {
        if (!TextUtils.isEmpty(binding.input1.text)) {
            binding.input1.selectAll()
        }
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

        binding.input1.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int,
                ) {}

                override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int,
                ) {
                    presenter.changeText(binding.input1.text.toString())
                }

                override fun afterTextChanged(editable: Editable) {}
            },
        )

        list.adapter = adapter
        fv.findViewById<View>(R.id.btn_1).setOnClickListener { clearInput() }
    }

    private fun clearInput() {
        binding.input1.setText("")
    }

    /**
     * Callback for user clicked any queried word.

     * @param text the text of the row which user clicked
     */
    private fun onRowClicked(text: String) {
        val intent = WordActivity.createIntent(requireActivity(), text)
        startActivity(intent)
    }
}
