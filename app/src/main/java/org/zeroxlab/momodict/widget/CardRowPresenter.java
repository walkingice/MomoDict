package org.zeroxlab.momodict.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.model.Card;

public class CardRowPresenter implements SelectorAdapter.Presenter<Card> {

    View.OnClickListener mListener;
    View.OnLongClickListener mLongListener;

    public CardRowPresenter(View.OnClickListener listener,
                            View.OnLongClickListener longListener) {
        mListener = listener;
        mLongListener = longListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.list_item_card_row, parent, false);
        return new InnerViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Card item) {
        InnerViewHolder holder = (InnerViewHolder) viewHolder;
        holder.iText1.setText(item.wordStr);
        holder.itemView.setOnClickListener(view -> {
            view.setTag(item.wordStr);
            mListener.onClick(view);
        });
        holder.itemView.setOnLongClickListener(view -> {
            view.setTag(item.wordStr);
            mLongListener.onLongClick(view);
            return true;
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
