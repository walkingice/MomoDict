package org.zeroxlab.momodict.widget

import android.content.Context
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import org.zeroxlab.momodict.R

import java.io.File

class FileRowPresenter(ctx: Context, private val mListener: View.OnClickListener) : SelectorAdapter.Presenter<FileRowPresenter.Item> {
    private val mHandler: Handler

    init {
        mHandler = Handler(ctx.mainLooper)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewGroup = inflater.inflate(
                R.layout.list_item_file_row, parent, false) as ViewGroup
        return InnerViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, item: Item) {
        val holder = viewHolder as InnerViewHolder
        if (item.file.isDirectory) {
            holder.iTextView.text = item.display
            holder.iImg.visibility = View.VISIBLE
        } else if (item.file.isFile) {
            holder.iTextView.text = item.display
            holder.iImg.visibility = View.INVISIBLE
        } else {
            holder.iTextView.text = "//Unknown//"
        }

        holder.itemView.isEnabled = item.file.canRead()
        holder.itemView.setOnClickListener { view ->
            view.tag = item.file

            // Delay to show ripple effect
            mHandler.postDelayed({ mListener.onClick(view) }, 250)
        }
    }

    override fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    class Item(var display: String, var file: File)

    internal inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iImg: ImageView
        var iTextView: TextView

        init {
            iTextView = view.findViewById<View>(R.id.text_1) as TextView
            iImg = view.findViewById<View>(R.id.img_1) as ImageView
        }
    }
}
