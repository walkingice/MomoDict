package org.zeroxlab.momodict.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * This is a listener to for any message from fragments
 */
public interface FragmentListener {
    enum TYPE {
        START_ACTIVITY
    }

    void onNotified(@Nullable Fragment from,
                    @NonNull TYPE type,
                    @Nullable Object payload);
}
