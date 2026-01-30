package cc.jchu.momodict.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.R
import cc.jchu.momodict.model.Record

class HistoryRowPresenter(
    private var clickCb: (v: View) -> Unit,
    private var longClickCb: (v: View) -> Boolean,
) : SelectorAdapter.Presenter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context)
            .let { it.inflate(R.layout.history_row, parent, false) }
            .let { InnerViewHolder(it) }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        item: Any?,
    ) {
        val item = item as Record
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
