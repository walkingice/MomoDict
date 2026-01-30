package cc.jchu.momodict.widget

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.jchu.momodict.R
import java.io.File

class FileRowPresenter(ctx: Context, private val callback: (v: View) -> Unit) :
    SelectorAdapter.Presenter {
    private val mHandler: Handler = Handler(ctx.mainLooper)

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context)
            .let { it.inflate(R.layout.list_item_file_row, parent, false) }
            .let { InnerViewHolder(it) }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        item: Any?,
    ) {
        val holder = viewHolder as InnerViewHolder
        val item = item as Item
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

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    data class Item(var display: String, var file: File)

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iImg: ImageView = view.findViewById<View>(R.id.img_1) as ImageView
        var iTextView: TextView = view.findViewById<View>(R.id.text_1) as TextView
    }
}
