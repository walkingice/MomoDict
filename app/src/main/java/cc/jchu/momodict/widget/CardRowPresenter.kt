package cc.jchu.momodict.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.R
import cc.jchu.momodict.model.Card

class CardRowPresenter(
    private val clickCallback: (v: View) -> Unit,
    private val longClickCallback: (v: View) -> Unit,
) : SelectorAdapter.Presenter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item_card_row, parent, false)
            .let { view -> view as ViewGroup }
            .let { viewGroup -> InnerViewHolder(viewGroup) }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        item: Any?,
    ) {
        val holder = viewHolder as InnerViewHolder

        val item = item as Card
        holder.iText1.text = item.wordStr

        holder.itemView.setOnClickListener { view ->
            view.tag = item.wordStr
            clickCallback(view)
            true
        }

        holder.itemView.setOnLongClickListener { view ->
            view.tag = item.wordStr
            longClickCallback(view)
            true
        }
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    internal inner class InnerViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var iText1: TextView = view.findViewById<View>(R.id.text_1) as TextView
    }
}
