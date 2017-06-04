package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch

import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.WordCardPresenter

import java.util.Date
import java.util.HashMap
import java.util.NoSuchElementException

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * A fragment to display detail of a word. Usually is translation from dictionaries.
 */
class WordFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private var mSwitch: Switch? = null
    private var mCtrl: Controller? = null
    private var mAdapter: SelectorAdapter? = null
    private var mKeyWord: String? = null

    private var mCard: Card? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(context)
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, WordCardPresenter())
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_word, container, false)
        initViews(fragmentView)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        mKeyWord = arguments.getString(ARG_KEYWORD)
        onDisplayDetail(mKeyWord!!)

        mSwitch!!.setOnCheckedChangeListener(null)

        // if the keyword is already stored as memo, retrieve it.
        // otherwise create a new Card
        mCtrl!!.cards
                .filter { card -> mKeyWord == card.wordStr }
                .first()
                .subscribe(
                        { card ->
                            // keyword stored
                            mCard = card
                            mSwitch!!.isChecked = true
                            mSwitch!!.setOnCheckedChangeListener(this)
                        }
                ) { e ->
                    // keyword not stored
                    if (e is NoSuchElementException) {
                        mCard = Card()
                        mCard!!.wordStr = mKeyWord
                        mSwitch!!.isChecked = false
                    }
                    mSwitch!!.setOnCheckedChangeListener(this)
                }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, checked: Boolean) {
        if (checked) {
            mCard!!.time = Date()
            mCtrl!!.setCard(mCard!!)
        } else {
            mCtrl!!.removeCards(mKeyWord!!)
        }
    }

    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as RecyclerView
        list.adapter = mAdapter

        mSwitch = fv.findViewById(R.id.control_1) as Switch
        mSwitch!!.setOnCheckedChangeListener { v, checked -> }
    }

    private fun onDisplayDetail(target: String) {
        if (activity is FragmentListener) {
            (activity as FragmentListener).onNotified(this,
                    FragmentListener.TYPE.UPDATE_TITLE,
                    target)
        }

        mAdapter!!.clear()
        updateRecord(target)

        // get translation of keyword from each dictionaries
        Observable.just(target)
                .subscribeOn(Schedulers.io())
                .flatMap<Entry> { keyWord -> mCtrl!!.getEntries(keyWord) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { entry -> mAdapter!!.addItem(entry, SelectorAdapter.Type.A) },
                        { e -> e.printStackTrace() }
                ) { mAdapter!!.notifyDataSetChanged() }
    }

    private fun updateRecord(target: String) {
        mCtrl!!.records
                .filter { record -> TextUtils.equals(target, record.wordStr) }
                .toList()
                .subscribe { list ->
                    val record = if (list.size == 0) Record() else list[0]
                    record.wordStr = if (TextUtils.isEmpty(record.wordStr)) target else record.wordStr
                    record.count += 1
                    record.time = Date()
                    mCtrl!!.setRecord(record)
                }
    }

    companion object {

        private val ARG_KEYWORD = "key_word"

        fun newInstance(keyWord: String): WordFragment {
            val fragment = WordFragment()
            val bundle = Bundle()
            bundle.putString(ARG_KEYWORD, keyWord)
            fragment.arguments = bundle
            return fragment
        }
    }
}
