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
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.model.Record;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordCardPresenter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mKeyWord = getArguments().getString(ARG_KEYWORD);
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

        mAdapter.clear();
        updateRecord(target);
        Observable.just(target)
                .subscribeOn(Schedulers.io())
                .flatMap((keyWord) -> mCtrl.getEntries(keyWord))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (entry) -> mAdapter.addItem(entry, SelectorAdapter.Type.A),
                        (e) -> e.printStackTrace(),
                        () -> mAdapter.notifyDataSetChanged());
    }

    private void updateRecord(@NonNull String target) {
        mCtrl.getRecords()
                .filter((record -> TextUtils.equals(target, record.wordStr)))
                .toList()
                .subscribe((list) -> {
                    Record record = (list.size() == 0) ? new Record() : list.get(0);
                    record.wordStr = (TextUtils.isEmpty(record.wordStr)) ? target : record.wordStr;
                    record.count += 1;
                    record.time = new Date();
                    mCtrl.setRecord(record);
                });
    }
}
