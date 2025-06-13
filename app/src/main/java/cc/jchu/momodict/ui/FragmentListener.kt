package cc.jchu.momodict.ui

import androidx.fragment.app.Fragment

/**
 * This is a listener to for any message from fragments
 */
interface FragmentListener {
    enum class TYPE {
        START_ACTIVITY,
        UPDATE_TITLE,
        POP_FRAGMENT,
        VIEW_ACTION
    }

    fun onNotified(from: androidx.fragment.app.Fragment?,
                   type: TYPE,
                   payload: Any?)
}
