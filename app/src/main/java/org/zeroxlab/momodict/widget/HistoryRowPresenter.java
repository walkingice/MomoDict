package org.zeroxlab.momodict.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.model.Record;

public class HistoryRowPresenter implements SelectorAdapter.Presenter<Record> {

    View.OnClickListener mListener;
    View.OnLongClickListener mLongListener;

    public HistoryRowPresenter(View.OnClickListener listener,
                               View.OnLongClickListener longListener) {
        mListener = listener;
        mLongListener = longListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.history_row, parent, false);
        return new InnerViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final Record item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        holder.iText1.setText(item.wordStr);
        holder.iText2.setText(item.count + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(item.wordStr);
                mListener.onClick(view);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setTag(item.wordStr);
                mLongListener.onLongClick(view);
                return true;
            }
        });
    }

    @Override
    public void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView iText1;
        TextView iText2;

        public InnerViewHolder(View view) {
            super(view);
            iText1 = (TextView) view.findViewById(R.id.text_1);
            iText2 = (TextView) view.findViewById(R.id.text_2);
        }
    }
}
