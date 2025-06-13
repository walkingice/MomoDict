package cc.jchu.momodict.input

import cc.jchu.momodict.model.Entry

interface InputContract {
    interface View {
        /**
         * To update status of Input view. If there is not any available dictionary, disable it.
         */
        fun onEnableInput(enabled: Boolean)

        fun inputSelectAll()

        fun onUpdateList(entries: List<Entry>)

        fun setLoading(isLoading: Boolean)
    }

    interface Presenter {
        fun changeText(text: String)
        fun onResume()
    }
}
