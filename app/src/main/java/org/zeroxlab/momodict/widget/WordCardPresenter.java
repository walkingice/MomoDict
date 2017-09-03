package org.zeroxlab.momodict.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.model.Entry;

public class WordCardPresenter implements SelectorAdapter.Presenter<Entry> {


    public WordCardPresenter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.list_item_word_row, parent, false);
        return new InnerViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Entry item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        holder.iText1.setText(item.source);
        holder.iText2.setText(item.data);
    }

    @Override
    public void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView iText1;
        TextView iText2;

        InnerViewHolder(View view) {
            super(view);
            iText1 = (TextView) view.findViewById(R.id.text_1);
            iText2 = (TextView) view.findViewById(R.id.text_2);
        }
    }
}
