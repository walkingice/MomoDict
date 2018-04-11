package org.zeroxlab.momodict.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.Momodict
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.WordActivity
import org.zeroxlab.momodict.widget.BackKeyHandler
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.ViewPagerFocusable
import org.zeroxlab.momodict.widget.WordRowPresenter
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.fragment_input.input_1 as mInput

/**
 * Fragment to provide UI which user can input a text to query, and display a list for queried text.
 */
class InputFragment : Fragment(), BackKeyHandler, ViewPagerFocusable {

    private var mAdapter: SelectorAdapter? = null
    private var mCtrl: Controller? = null

    /**
     * User input won't be send to mCtrl directly. Instead, send to here so we have more flexibility
     * to use mCtrl.
     */
    private var mQuery: Subject<String, String>? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        mCtrl = Controller(activity)

        // create adapter for RecyclerView. Adapter handles two kinds of row, one for "dictionary"
        // and another for "word".
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A,
                WordRowPresenter { view -> onRowClicked(view.tag as String) })
        mAdapter = SelectorAdapter(map)

        // This fragment might be destroy if user scroll to third Tab, so we have to re-create it
        // in onCreate callback.
        mQuery = PublishSubject.create<String>()
        // If user type quickly, do not query until user stop inputting.
        mQuery!!.debounce(INPUT_DELAY.toLong(), TimeUnit.MILLISECONDS)
                .concatMap { input -> mCtrl!!.queryEntries(input).toList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    mAdapter!!.clear()
                    for (entry in list) {
                        mAdapter!!.addItem(entry.wordStr, SelectorAdapter.Type.A)
                    }
                    mAdapter!!.notifyDataSetChanged()
                }) { e -> e.printStackTrace() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mQuery!!.onCompleted()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        initViews(fragmentView)
    }

    override fun onResume() {
        super.onResume()
        onUpdateInput()
        onUpdateList()
    }

    /**
     * Callback for [BackKeyHandler]. Called when user clicked back key.
     * If the input field is not empty, back-key-event will clear the field.
     * We regards this as "handled"

     * @return true if this fragment handled back-key-event
     */
    override fun backKeyHandled(): Boolean {
        if (TextUtils.isEmpty(mInput.text)) {
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
        mInput.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as RecyclerView
        val mgr = list.layoutManager as LinearLayoutManager
        val decoration = DividerItemDecoration(list.context,
                mgr.orientation)
        list.addItemDecoration(decoration)

        mInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                onUpdateList()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        list.adapter = mAdapter
        fv.findViewById<View>(R.id.btn_1).setOnClickListener { v -> clearInput() }
    }

    private fun clearInput() {
        mInput.setText("")
    }

    /**
     * To update status of Input view - disable or enable it.
     */
    private fun onUpdateInput() {
        // If there is no any available dictionary, disable Input view.
        mCtrl!!.books
                .count()
                .subscribe { count ->
                    mInput.isEnabled = count > 0
                    if (count > 0 && !TextUtils.isEmpty(mInput.text)) {
                        mInput.selectAll()
                    }
                }
    }

    /**
     * Update list according to user's input.
     */
    private fun onUpdateList() {
        val input = mInput.text.toString().trim { it <= ' ' }
        Log.d(TAG, "Input: " + input)
        mAdapter!!.clear()
        if (TextUtils.isEmpty(input)) {
            // User haven't input anything, just clear the list
            mAdapter!!.notifyDataSetChanged()

        } else {
            mQuery!!.onNext(input)
        }
    }

    /**
     * Callback for user clicked any queried word.

     * @param text the text of the row which user clicked
     */
    private fun onRowClicked(text: String) {
        val intent = WordActivity.createIntent(activity, text)
        startActivity(intent)
    }

    companion object {

        private val TAG = Momodict.TAG
        private val INPUT_DELAY = 300
    }
}
