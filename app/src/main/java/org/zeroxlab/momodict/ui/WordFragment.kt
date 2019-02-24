package org.zeroxlab.momodict.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.Date

/**
 * A fragment to display detail of a word. Usually is translation from dictionaries.
 */
class WordFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var mSwitch: Switch
    private lateinit var mCtrl: Controller
    private lateinit var mAdapter: SelectorAdapter
    private lateinit var mKeyWord: String

    private var mCard: Card? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(context!!) // FIXME: remove !!
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, WordCardPresenter())
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_word, container, false).also {
            initViews(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mKeyWord = arguments?.getString(ARG_KEYWORD) ?: return
        onDisplayDetail(mKeyWord)

        mSwitch.setOnCheckedChangeListener(null)

        // if the keyword is already stored as memo, retrieve it.
        // otherwise create a new Card
        mCtrl.getCards()
                .filter { card -> mKeyWord == card.wordStr }
                .first()
                .subscribe(
                        { card ->
                            // keyword stored
                            mCard = card
                            mSwitch.isChecked = true
                            mSwitch.setOnCheckedChangeListener(this)
                        }
                ) { e ->
                    // keyword not stored
                    if (e is NoSuchElementException) {
                        mCard = Card(mKeyWord)
                        mSwitch.isChecked = false
                    }
                    mSwitch.setOnCheckedChangeListener(this)
                }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, checked: Boolean) {
        if (checked) {
            mCard!!.time = Date()
            mCtrl.setCard(mCard!!)
        } else {
            mCtrl.removeCards(mKeyWord)
        }
    }

    private fun initViews(fv: View) {
        val list = fv.findViewById(R.id.list) as RecyclerView
        list.adapter = mAdapter

        mSwitch = fv.findViewById(R.id.control_1) as Switch
        mSwitch.setOnCheckedChangeListener { v, checked -> }
    }

    private fun onDisplayDetail(target: String) {
        if (activity is FragmentListener) {
            (activity as FragmentListener).onNotified(this,
                    FragmentListener.TYPE.UPDATE_TITLE,
                    target)
        }

        mAdapter.clear()
        updateRecord(target)

        // get translation of keyword from each dictionaries
        Observable.just(target)
                .subscribeOn(Schedulers.io())
                .flatMap<Entry> { keyWord -> mCtrl.getEntries(keyWord) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { entry -> mAdapter.addItem(entry, SelectorAdapter.Type.A) },
                        { e -> e.printStackTrace() }
                ) { mAdapter.notifyDataSetChanged() }
    }

    private fun updateRecord(target: String) {
        mCtrl.getRecords()
                .filter { record -> TextUtils.equals(target, record.wordStr) }
                .toList()
                .subscribe { list ->
                    val record = (if (list.size == 0) Record(target) else list[0]).also {
                        it.wordStr = if (it.wordStr.isNullOrEmpty()) target else it.wordStr
                        it.count += 1
                        it.time = Date()
                    }
                    mCtrl.setRecord(record)
                }
    }

    companion object {

        private val ARG_KEYWORD = "key_word"

        fun newInstance(keyWord: String): WordFragment {
            val fragment = WordFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_KEYWORD, keyWord)
            }
            return fragment
        }
    }
}
