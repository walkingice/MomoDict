package org.zeroxlab.momodict.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WordRowPresenter(private val callback: (v: View) -> Unit) : SelectorAdapter.Presenter<String> {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
                .let { view -> InnerViewHolder(view) }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: String) {
        val holder = viewHolder as InnerViewHolder
        holder.iTextView.text = item
        holder.itemView.setOnClickListener { view ->
            view.tag = item
            callback(view)
        }
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iTextView: TextView = view.findViewById<View>(android.R.id.text1) as TextView
    }
}
