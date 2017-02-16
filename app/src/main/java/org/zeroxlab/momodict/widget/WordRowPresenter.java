package org.zeroxlab.momodict.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zeroxlab.momodict.R;

public class WordRowPresenter implements SelectorAdapter.Presenter<String> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.list_item_with_arrow, parent, false);
        return new InnerViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, String item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        holder.iTextView.setText(item);
    }

    @Override
    public void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView iTextView;

        public InnerViewHolder(View view) {
            super(view);
            iTextView = (TextView) view.findViewById(R.id.text_1);
        }
    }
}
