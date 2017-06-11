package org.zeroxlab.momodict.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WordRowPresenter implements SelectorAdapter.Presenter<String> {

    View.OnClickListener mListener;

    public WordRowPresenter(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new InnerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final String item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        holder.iTextView.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(item);
                mListener.onClick(view);
            }
        });
    }

    @Override
    public void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView iTextView;

        public InnerViewHolder(View view) {
            super(view);
            iTextView = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
