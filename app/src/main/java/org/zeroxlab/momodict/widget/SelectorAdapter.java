package org.zeroxlab.momodict.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A RecyclerView Adapter, to use PresenterSelector to decide corresponding presenter for specific
 * data item. When adding any item into this adapter, we should specify a type at the same time.
 * <p>
 * This Adapter is similar with ArrayObjectAdapter, but simpler. Instead of using 'instanceof'
 * operator, this Adapter use enum Type to get better performance.
 */
public class SelectorAdapter extends RecyclerView.Adapter {

    final private PresenterSelector mSelector;
    final private List<Object> mData = new ArrayList<>();
    final private List<Type> mTypes = new ArrayList<>();
    final private Type[] mTypesArray = Type.values();

    public SelectorAdapter(@NonNull Map<Type, Presenter> map) {
        mSelector = new DefaultSelector(map);
    }

    public SelectorAdapter(@NonNull PresenterSelector selector) {
        mSelector = selector;
    }

    /**
     * Add data item into this adapter. Caller should also specify type for that item.
     *
     * @param obj  Data item to be added
     * @param type Which type of this item.
     */
    public void addItem(@NonNull Object obj, @NonNull Type type) {
        mData.add(obj);
        mTypes.add(type);
    }

    public void clear() {
        mData.clear();
        mTypes.clear();
        notifyDataSetChanged();
    }

    public void replace(int location, @NonNull Object obj, @NonNull Type type) {
        mData.set(location, obj);
        mTypes.set(location, type);
        notifyItemChanged(location);
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position).ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Type type = mTypesArray[viewType];
        Presenter presenter = mSelector.getPresenter(type);
        return presenter.onCreateViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Type type = mTypes.get(position);
        Object data = mData.get(position);
        Presenter presenter = mSelector.getPresenter(type);
        presenter.onBindViewHolder(holder, data);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Pre-defined types, to be used when adding items.
     */
    public enum Type {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O
    }

    public interface Presenter<T> {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, T item);

        void onUnbindViewHolder(RecyclerView.ViewHolder viewHolder);
    }

    /**
     * A presenter selector which be used by SelectorAdapter
     */
    public static interface PresenterSelector {

        /**
         * To return a presenter for specific Type.
         *
         * @param type
         * @return A presenter for corresponding Type
         */
        public abstract Presenter getPresenter(Type type);
    }

    private class DefaultSelector implements PresenterSelector {
        Map<Type, Presenter> mMap = new HashMap<>();

        DefaultSelector(Map<Type, Presenter> map) {
            mMap.putAll(map);
        }

        @Override
        public Presenter getPresenter(Type type) {
            return mMap.get(type);
        }
    }
}
