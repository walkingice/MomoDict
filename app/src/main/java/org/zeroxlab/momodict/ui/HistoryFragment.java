package org.zeroxlab.momodict.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zeroxlab.momodict.R;

public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    private void initViews(View fv) {
    }

}
