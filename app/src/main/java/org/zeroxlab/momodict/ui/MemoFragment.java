package org.zeroxlab.momodict.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import org.zeroxlab.momodict.widget.CardRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.ViewPagerFocusable;

import java.util.HashMap;
import java.util.Map;

public class MemoFragment extends Fragment implements ViewPagerFocusable {

    private Controller mCtrl;
    private SelectorAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new CardRowPresenter(
                (view) -> onRowClicked((String) view.getTag()),
                (view) -> {
                    onRowLongClicked((String) view.getTag());
                    return true;
                }));

        mCtrl = new Controller(getActivity());
        mAdapter = new SelectorAdapter(map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_memo, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onUpdateList();
    }

    @Override
    public void onViewPagerFocused() {
        final View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        onUpdateList();
    }


    private void initViews(@NonNull View fv) {
        final RecyclerView list = (RecyclerView) fv.findViewById(R.id.list);
        final LinearLayoutManager mgr = (LinearLayoutManager) list.getLayoutManager();
        final DividerItemDecoration decoration = new DividerItemDecoration(list.getContext(),
                mgr.getOrientation());
        list.addItemDecoration(decoration);
        list.setAdapter(mAdapter);
    }

    private void onRowClicked(String keyWord) {
        final Intent intent = WordActivity.createIntent(getActivity(), keyWord);
        startActivity(intent);
    }

    private void onRowLongClicked(@NonNull final String keyWord) {
        new AlertDialog.Builder(getActivity())
                .setTitle(keyWord)
                .setPositiveButton("Remove", (dialogInterface, i) -> {
                    mCtrl.removeCards(keyWord);
                    onUpdateList();
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                    // do nothing on canceling
                })
                .create()
                .show();
    }

    private void onUpdateList() {
        mAdapter.clear();
        mCtrl.getCards().subscribe(
                (card) -> mAdapter.addItem(card, SelectorAdapter.Type.A),
                (e) -> e.printStackTrace(),
                () -> mAdapter.notifyDataSetChanged()
        );
    }
}
