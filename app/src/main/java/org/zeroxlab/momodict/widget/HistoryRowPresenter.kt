package org.zeroxlab.momodict.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Record

class HistoryRowPresenter(internal var mListener: View.OnClickListener,
                          internal var mLongListener: View.OnLongClickListener) : SelectorAdapter.Presenter<Record> {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewGroup = inflater.inflate(
                R.layout.history_row, parent, false) as ViewGroup
        return InnerViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: Record) {
        val holder = viewHolder as InnerViewHolder
        holder.iText1.text = item.wordStr
        holder.iText2.text = item.count.toString() + ""
        holder.itemView.setOnClickListener { view ->
            view.tag = item.wordStr
            mListener.onClick(view)
        }
        holder.itemView.setOnLongClickListener { view ->
            view.tag = item.wordStr
            mLongListener.onLongClick(view)
            true
        }
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
