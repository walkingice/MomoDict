package cc.jchu.momodict.widget

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class ConfirmDialogFragment : DialogFragment() {
    private var mPosListener = sEmptyClickCallback
    private var mNegListener = sEmptyClickCallback

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val title = arguments.getInt(sTitle)

        return AlertDialog.Builder(activity)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok, mPosListener)
            .setNegativeButton(android.R.string.cancel, mNegListener)
            .create()
    }

    fun setPositiveCallback(listener: DialogInterface.OnClickListener) {
        mPosListener = listener
    }

    fun setNegativeCallback(listener: DialogInterface.OnClickListener) {
        mNegListener = listener
    }

    companion object {
        private val sTitle = "title"

        fun newInstance(posListener: DialogInterface.OnClickListener): ConfirmDialogFragment {
            val fragment = ConfirmDialogFragment()
            fragment.setPositiveCallback(posListener)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        private val sEmptyClickCallback: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialogInterface, i ->
                // do nothing
            }
    }
}
