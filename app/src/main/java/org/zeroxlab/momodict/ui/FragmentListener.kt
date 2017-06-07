package org.zeroxlab.momodict.ui

import android.support.v4.app.Fragment

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

    fun onNotified(from: Fragment?,
                   type: TYPE,
                   payload: Any?)
}
