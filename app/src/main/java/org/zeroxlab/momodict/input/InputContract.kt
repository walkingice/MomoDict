package org.zeroxlab.momodict.input

import org.zeroxlab.momodict.model.Entry

interface InputContract {
    interface View {
        /**
         * To update status of Input view. If there is not any available dictionary, disable it.
         */
        fun onEnableInput(enabled: Boolean)

        fun inputSelectAll()

        fun onUpdateList(entries: List<Entry>)
    }

    interface Presenter {
        fun changeText(text: String)
        fun onResume()
        fun onDestroy()
    }
}
