package org.zeroxlab.momodict.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.zeroxlab.momodict.R;

import java.io.File;

public class FileRowPresenter implements SelectorAdapter.Presenter<FileRowPresenter.Item> {

    private View.OnClickListener mListener;
    private Handler mHandler;

    public FileRowPresenter(@NonNull Context ctx, @NonNull View.OnClickListener listener) {
        mListener = listener;
        mHandler = new Handler(ctx.getMainLooper());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.list_item_file_row, parent, false);
        return new InnerViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final Item item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        if (item.file.isDirectory()) {
            holder.iTextView.setText(item.display);
            holder.iImg.setVisibility(View.VISIBLE);
        } else if (item.file.isFile()) {
            holder.iTextView.setText(item.display);
            holder.iImg.setVisibility(View.INVISIBLE);
        } else {
            holder.iTextView.setText("//Unknown//");
        }

        holder.itemView.setEnabled(item.file.canRead());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setTag(item.file);

                // Delay to show ripple effect
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onClick(view);
                    }
                }, 250);
            }
        });
    }

    @Override
    public void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    public static class Item {
        public String display;
        public File file;

        public Item(String display, File file) {
            this.display = display;
            this.file = file;
        }
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        ImageView iImg;
        TextView iTextView;

        public InnerViewHolder(View view) {
            super(view);
            iTextView = (TextView) view.findViewById(R.id.text_1);
            iImg = (ImageView) view.findViewById(R.id.img_1);
        }
    }
}
