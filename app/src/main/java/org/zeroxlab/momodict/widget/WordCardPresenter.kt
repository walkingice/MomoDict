package org.zeroxlab.momodict.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Entry

class WordCardPresenter : SelectorAdapter.Presenter<Entry> {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context)
                .let { it.inflate(R.layout.list_item_word_row, parent, false) }
                .let { InnerViewHolder(it) }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: Entry) {
        val holder = viewHolder as InnerViewHolder
        holder.iText1.text = item.source
        holder.iText2.text = item.data
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iText1: TextView = view.findViewById<View>(R.id.text_1) as TextView
        var iText2: TextView = view.findViewById<View>(R.id.text_2) as TextView
    }
}
