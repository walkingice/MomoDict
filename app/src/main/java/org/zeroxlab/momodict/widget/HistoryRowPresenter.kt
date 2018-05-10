package org.zeroxlab.momodict.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Record

class HistoryRowPresenter(private var clickCb: (v: View) -> Unit,
                          private var longClickCb: (v: View) -> Boolean) : SelectorAdapter.Presenter<Record> {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context)
                .let { it.inflate(R.layout.history_row, parent, false) }
                .let { InnerViewHolder(it) }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: Record) {
        with(viewHolder as InnerViewHolder) {
            iText1.text = item.wordStr
            iText2.text = item.count.toString() + ""
            itemView.setOnClickListener { view ->
                view.tag = item.wordStr
                clickCb(view)
            }

            itemView.setOnLongClickListener { view ->
                view.tag = item.wordStr
                longClickCb(view)
                true
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iText1: TextView = view.findViewById<View>(R.id.text_1) as TextView
        var iText2: TextView = view.findViewById<View>(R.id.text_2) as TextView
    }
}
