package org.zeroxlab.momodict.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.zeroxlab.momodict.Controller
import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.widget.SelectorAdapter
import org.zeroxlab.momodict.widget.WordCardPresenter
import java.util.Date

/**
 * A fragment to display detail of a word. Usually is translation from dictionaries.
 */
class WordFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var mSwitch: Switch
    private lateinit var mCtrl: Controller
    private lateinit var mAdapter: SelectorAdapter
    private lateinit var mKeyWord: String

    private lateinit var mCard: Card

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mCtrl = Controller(requireActivity())
        val map = HashMap<SelectorAdapter.Type, SelectorAdapter.Presenter<*>>()
        map.put(SelectorAdapter.Type.A, WordCardPresenter())
        mAdapter = SelectorAdapter(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word, container, false).also {
            initViews(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mKeyWord = arguments?.getString(ARG_KEYWORD) ?: return
        mCard = Card(mKeyWord) // fallback
        onDisplayDetail(mKeyWord)

        mSwitch.setOnCheckedChangeListener(null)

        // if the keyword is already stored as memo, retrieve it.
        // otherwise create a new Card
        mCtrl.getCards(requireActivity().lifecycle.coroutineScope) {
            try {
                val card = it.first { card -> mKeyWord == card.wordStr }
                mCard = card
                mSwitch.isChecked = true
            } catch (e: NoSuchElementException) {
                // TODO: Using exception to catch 'if card.size == 0' is not a good idea.
                mCard = Card(mKeyWord)
                mSwitch.isChecked = false
            }
            mSwitch.setOnCheckedChangeListener(this)
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, checked: Boolean) {
        if (checked) {
            mCard.time = Date()
            mCtrl.setCard(mCard)
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
            (activity as FragmentListener).onNotified(
                this,
                FragmentListener.TYPE.UPDATE_TITLE,
                target
            )
        }

        mAdapter.clear()
        updateRecord(target)

        // get translation of keyword from each dictionaries
        lifecycle.coroutineScope.launch {
            mCtrl.getEntries(target)
                .forEach { mAdapter.addItem(it, SelectorAdapter.Type.A) }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun updateRecord(target: String) {
        requireActivity().lifecycle.coroutineScope.launch {
            val records = mCtrl.getRecords()
            val list = records.filter { record -> TextUtils.equals(target, record.wordStr) }
            val record = (if (list.isEmpty()) Record(target) else list[0]).also { r ->
                r.wordStr = if (r.wordStr.isEmpty()) target else r.wordStr
                r.count += 1
                r.time = Date()
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
