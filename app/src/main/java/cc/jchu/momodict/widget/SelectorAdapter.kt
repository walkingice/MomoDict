package cc.jchu.momodict.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * A RecyclerView Adapter, to use PresenterSelector to decide corresponding presenter for specific
 * note item. When adding any item into this adapter, we should specify a type at the same time.
 *
 *
 * This Adapter is similar with ArrayObjectAdapter, but simpler. Instead of using 'instanceof'
 * operator, this Adapter use enum Type to get better performance.
 *
 * TODO: generic was removed for simplification, consider to add it back in future.
 */
class SelectorAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val mSelector: PresenterSelector
    private val mData: MutableList<Any> = ArrayList<Any>()
    private val mTypes: MutableList<Type> = ArrayList<Type>()
    private val mTypesArray: Array<Type> = Type.entries.toTypedArray()

    constructor(map: MutableMap<Type, Presenter>) {
        mSelector = DefaultSelector(map)
    }

    constructor(selector: PresenterSelector) {
        mSelector = selector
    }

    /**
     * Add note item into this adapter. Caller should also specify type for that item.
     *
     * @param obj  Data item to be added
     * @param type Which type of this item.
     */
    fun addItem(
        obj: Any,
        type: Type,
    ) {
        mData.add(obj)
        mTypes.add(type)
    }

    fun clear() {
        mData.clear()
        mTypes.clear()
        notifyDataSetChanged()
    }

    fun replace(
        location: Int,
        obj: Any,
        type: Type,
    ) {
        mData.set(location, obj)
        mTypes.set(location, type)
        notifyItemChanged(location)
    }

    override fun getItemViewType(position: Int): Int {
        return mTypes.get(position).ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val type = mTypesArray[viewType]
        val presenter = mSelector.getPresenter(type)
        return presenter!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val type = mTypes.get(position)
        val data = mData.get(position)
        mSelector.getPresenter(type)?.onBindViewHolder(holder, data)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * Pre-defined types, to be used when adding items.
     */
    enum class Type {
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O,
    }

    interface Presenter {
        fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

        fun onBindViewHolder(
            viewHolder: RecyclerView.ViewHolder,
            item: Any?,
        )

        fun onUnbindViewHolder(viewHolder: RecyclerView.ViewHolder)
    }

    /**
     * A presenter selector which be used by SelectorAdapter
     */
    interface PresenterSelector {
        /**
         * To return a presenter for specific Type.
         *
         * @param type
         * @return A presenter for corresponding Type
         */
        fun getPresenter(type: Type?): Presenter?
    }

    private inner class DefaultSelector(map: MutableMap<Type, Presenter>) : PresenterSelector {
        var mMap: MutableMap<Type, Presenter> = HashMap()

        init {
            mMap.putAll(map)
        }

        override fun getPresenter(type: Type?): Presenter? {
            return mMap.get(type)
        }
    }
}
