package org.zeroxlab.momodict.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import org.zeroxlab.momodict.R;

public class ConfirmDialogFragment extends DialogFragment {

    private static String sTitle = "title";

    public static ConfirmDialogFragment newInstance(DialogInterface.OnClickListener posListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setPositiveCallback(posListener);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private DialogInterface.OnClickListener mPosListener = sEmptyClickCallback;
    private DialogInterface.OnClickListener mNegListener = sEmptyClickCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(sTitle);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, mPosListener)
                .setNegativeButton(android.R.string.cancel, mNegListener)
                .create();
    }

    private static DialogInterface.OnClickListener sEmptyClickCallback = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // do nothing
        }
    };

    public void setPositiveCallback(DialogInterface.OnClickListener listener) {
        mPosListener = listener;
    }

    public void setNegativeCallback(DialogInterface.OnClickListener listener) {
        mNegListener = listener;
    }
}
