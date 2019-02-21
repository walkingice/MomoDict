package org.zeroxlab.momodict.widget

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import org.zeroxlab.momodict.R
import org.zeroxlab.momodict.model.Book

class BookRowPresenter(listener: View.OnClickListener) : SelectorAdapter.Presenter<Book> {

    val rmListener = listener

    override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val group = inflater.inflate(R.layout.list_item_expandable, parent, false) as ViewGroup
        val bookDetails = inflater.inflate(R.layout.book_detail, group, false) as ViewGroup
        val holder = InnerViewHolder(group, bookDetails)
        group.setOnClickListener({ v -> toggleExpand(holder) })
        return holder
    }

    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: Book) {
        val holder = viewHolder as InnerViewHolder
        holder.titleText.text = item.bookName
        holder.arrowIcon.setImageLevel(IMG_LEVEL_UP)
        holder.description.text = """
                |Author: ${item.author}
                |Words: ${item.wordCount}
                |Date: ${item.date}
                |Description: ${item.description}
        """.trimMargin()
        holder.rmBtn.setOnClickListener({ v ->
            v.tag = item
            rmListener.onClick(v)
        })
    }

    override fun onUnbindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {}

    private fun toggleExpand(holder: InnerViewHolder) {
        val drawable = holder.arrowIcon.drawable
        drawable.level = if (drawable.level == IMG_LEVEL_UP) IMG_LEVEL_DOWN else IMG_LEVEL_UP
        holder.details.visibility = if (drawable.level == IMG_LEVEL_UP) View.GONE else View.VISIBLE
    }

    internal inner class InnerViewHolder(view: View, bookDetails: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.text_1) as TextView
        val arrowIcon: ImageView = view.findViewById(R.id.img_1) as ImageView
        val details: ViewGroup = view.findViewById(R.id.expand_details) as ViewGroup
        val rmBtn: View = bookDetails.findViewById(R.id.btn_2)
        val description: TextView = bookDetails.findViewById(R.id.text_2) as TextView

        init {
            details.addView(bookDetails)
        }
    }

    companion object {
        val IMG_LEVEL_DOWN = 1
        val IMG_LEVEL_LEFT = 2
        val IMG_LEVEL_UP = 3
    }
}
