package cc.jchu.momodict.widget

import android.content.Context
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import cc.jchu.momodict.R

import java.io.File

class FileRowPresenter(ctx: Context, private val callback: (v: View) -> Unit) : SelectorAdapter.Presenter<FileRowPresenter.Item> {

    private val mHandler: Handler = Handler(ctx.mainLooper)

    override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context)
                .let { it.inflate(R.layout.list_item_file_row, parent, false) }
                .let { InnerViewHolder(it) }
    }

    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: Item) {
        val holder = viewHolder as InnerViewHolder
        when {
            item.file.isDirectory -> {
                holder.iTextView.text = item.display
                holder.iImg.visibility = View.VISIBLE
            }
            item.file.isFile -> {
                holder.iTextView.text = item.display
                holder.iImg.visibility = View.INVISIBLE
            }
            else -> holder.iTextView.text = "//Unknown//"
        }

        holder.itemView.isEnabled = item.file.canRead()
        holder.itemView.setOnClickListener { view ->
            view.tag = item.file

            // Delay to show ripple effect
            mHandler.postDelayed({ callback(view) }, 250)
        }
    }

    override fun onUnbindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {}

    data class Item(var display: String, var file: File)

    internal inner class InnerViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var iImg: ImageView = view.findViewById<View>(R.id.img_1) as ImageView
        var iTextView: TextView = view.findViewById<View>(R.id.text_1) as TextView
    }
}
