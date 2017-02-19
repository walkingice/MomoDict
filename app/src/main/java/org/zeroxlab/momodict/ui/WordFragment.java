package org.zeroxlab.momodict.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zeroxlab.momodict.Momodict;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.db.Store;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Entry;

import java.util.List;

public class WordFragment extends Fragment {

    private TextView mText;

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_word, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String text = getActivity().getIntent().getStringExtra(Momodict.EXTRA_DATA_1);
        onDisplayDetail(text);
    }

    private void initViews(View fv) {
        mText = (TextView) fv.findViewById(R.id.text_1);
    }

    private void onDisplayDetail(@NonNull String target) {
        mText.setText("Loading details....");
        Runnable runnable = () -> {
            final StringBuilder sb = new StringBuilder();
            final Store store = new RealmStore(getContext());
            final List<Entry> entries = store.getEntries(target);
            for (Entry entry : entries) {
                sb.append("=======" + entries.indexOf(entry) + "=======\n");
                sb.append(entry.data);
            }

            getActivity().runOnUiThread(() -> mText.setText(sb.toString()));
        };
        Thread io = new Thread(runnable);
        io.start();
    }
}
