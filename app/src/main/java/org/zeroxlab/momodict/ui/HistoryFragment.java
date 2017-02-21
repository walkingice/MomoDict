package org.zeroxlab.momodict.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.zeroxlab.momodict.Controller;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.WordActivity;
import org.zeroxlab.momodict.model.Record;
import org.zeroxlab.momodict.widget.HistoryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private RecyclerView mList;
    private Controller mCtrl;
    private SelectorAdapter mAdapter;

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);
        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new HistoryRowPresenter((view) -> {
            onRowClicked((String) view.getTag());
        }, (view) -> {
            onRowLongClicked((String) view.getTag());
            return true;
        }));

        mCtrl = new Controller(getActivity());
        mAdapter = new SelectorAdapter(map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onUpdateList();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void initViews(View fv) {
        mList = (RecyclerView) fv.findViewById(R.id.list);
        LinearLayoutManager mgr = (LinearLayoutManager) mList.getLayoutManager();
        DividerItemDecoration decoration = new DividerItemDecoration(mList.getContext(),
                mgr.getOrientation());
        mList.addItemDecoration(decoration);
        mList.setAdapter(mAdapter);
    }

    private void onRowClicked(String keyWord) {
        Intent intent = WordActivity.createIntent(getActivity(), keyWord);
        startActivity(intent);
    }

    private void onRowLongClicked(@NonNull final String keyWord) {
        new AlertDialog.Builder(getActivity())
                .setTitle(keyWord)
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCtrl.removeRecord(keyWord);
                        onUpdateList();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing on canceling
                    }
                })
                .create()
                .show();
    }

    private void onUpdateList() {
        mAdapter.clear();
        List<Record> records = mCtrl.getRecords();
        for (Record r : records) {
            mAdapter.addItem(r, SelectorAdapter.Type.A);
        }

        mAdapter.notifyDataSetChanged();
    }
}
