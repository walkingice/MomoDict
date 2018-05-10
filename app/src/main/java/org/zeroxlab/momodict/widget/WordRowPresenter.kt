package org.zeroxlab.momodict.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WordRowPresenter(internal var mListener: View.OnClickListener) : SelectorAdapter.Presenter<String> {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return InnerViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: String) {
        val holder = viewHolder as InnerViewHolder
        holder.iTextView.text = item
        holder.itemView.setOnClickListener { view ->
            view.tag = item
            mListener.onClick(view)
        }
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iTextView: TextView

        init {
            iTextView = view.findViewById<View>(android.R.id.text1) as TextView
        }
    }
}
