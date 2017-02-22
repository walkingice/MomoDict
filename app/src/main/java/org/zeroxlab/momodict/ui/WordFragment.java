package org.zeroxlab.momodict.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zeroxlab.momodict.Controller;
import org.zeroxlab.momodict.Momodict;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Record;
import org.zeroxlab.momodict.model.Store;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordCardPresenter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFragment extends Fragment {

    private static final String ARG_KEYWORD = "key_word";
    private Controller mCtrl;
    private SelectorAdapter mAdapter;
    private String mKeyWord;

    public static WordFragment newInstance(@NonNull String keyWord) {
        WordFragment fragment = new WordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEYWORD, keyWord);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);
        mCtrl = new Controller(getContext());
        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new WordCardPresenter());
        mAdapter = new SelectorAdapter(map);

        mKeyWord = savedState.getString(ARG_KEYWORD);
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
        onDisplayDetail(mKeyWord);
    }

    private void initViews(View fv) {
        final RecyclerView list = (RecyclerView) fv.findViewById(R.id.list);
        list.setAdapter(mAdapter);
    }

    private void onDisplayDetail(@NonNull String target) {
        if (getActivity() instanceof FragmentListener) {
            ((FragmentListener) getActivity()).onNotified(this,
                    FragmentListener.TYPE.UPDATE_TITLE,
                    target);
        }

        Runnable runnable = () -> {
            final Store store = new RealmStore(getContext());
            final List<Entry> entries = store.getEntries(target);

            if (entries.size() <= 0) {
                return;
            }

            updateRecord(target);
            getActivity().runOnUiThread(() -> {
                mAdapter.clear();
                for (Entry entry : entries) {
                    mAdapter.addItem(entry, SelectorAdapter.Type.A);
                }
                mAdapter.notifyDataSetChanged();
            });
        };
        Thread io = new Thread(runnable);
        io.start();
    }

    private void updateRecord(@NonNull String target) {
        final List<Record> records = mCtrl.getRecords();
        Record record = null;
        for (Record r : records) {
            if (TextUtils.equals(target, r.wordStr)) {
                record = r;
                break;
            }
        }

        record = (record == null) ? new Record() : record;
        record.wordStr = (TextUtils.isEmpty(record.wordStr)) ? target : record.wordStr;
        record.count += 1;
        record.time = new Date();
        mCtrl.setRecord(record);
    }
}
