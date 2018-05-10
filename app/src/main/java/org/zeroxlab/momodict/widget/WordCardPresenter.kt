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
        val inflater = LayoutInflater.from(parent.context)
        val viewGroup = inflater.inflate(
                R.layout.list_item_word_row, parent, false) as ViewGroup
        return InnerViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: Entry) {
        val holder = viewHolder as InnerViewHolder
        holder.iText1.text = item.source
        holder.iText2.text = item.data
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iText1: TextView
        var iText2: TextView

        init {
            iText1 = view.findViewById<View>(R.id.text_1) as TextView
            iText2 = view.findViewById<View>(R.id.text_2) as TextView
        }
    }
}
