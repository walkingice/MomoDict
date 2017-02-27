package org.zeroxlab.momodict.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.zeroxlab.momodict.Controller;
import org.zeroxlab.momodict.Momodict;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.WordActivity;
import org.zeroxlab.momodict.widget.BackKeyHandler;
import org.zeroxlab.momodict.widget.DictionaryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;

public class InputFragment extends Fragment implements BackKeyHandler {

    private static final String TAG = Momodict.TAG;

    private SelectorAdapter mAdapter;
    private EditText mInput;
    private Controller mCtrl;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        mCtrl = new Controller(getActivity());
        final Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new DictionaryRowPresenter());
        map.put(SelectorAdapter.Type.B,
                new WordRowPresenter((view) -> onRowClicked((String) view.getTag())));
        mAdapter = new SelectorAdapter(map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_input, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onUpdateInput();
        onUpdateList();
    }

    @Override
    public boolean backKeyHandled() {
        if (TextUtils.isEmpty(mInput.getText())) {
            return false;
        } else {
            clearInput();
            return true;
        }
    }

    private void initViews(@NonNull View fv) {
        final RecyclerView list = (RecyclerView) fv.findViewById(R.id.list);
        final LinearLayoutManager mgr = (LinearLayoutManager) list.getLayoutManager();
        final DividerItemDecoration decoration = new DividerItemDecoration(list.getContext(),
                mgr.getOrientation());
        list.addItemDecoration(decoration);
        mInput = (EditText) fv.findViewById(R.id.input_1);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onUpdateList();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list.setAdapter(mAdapter);
        fv.findViewById(R.id.btn_1).setOnClickListener((v) -> clearInput());
    }

    private void clearInput() {
        mInput.setText("");
    }

    private void onUpdateInput() {
        mCtrl.getBooks()
                .count()
                .subscribe((count) -> {
                    mInput.setEnabled(count > 0);
                    if (count > 0 && !TextUtils.isEmpty(mInput.getText())) {
                        mInput.selectAll();
                    }
                });
    }

    private void onUpdateList() {
        final String input = mInput.getText().toString();
        Log.d(TAG, "Input: " + input);
        mAdapter.clear();
        if (TextUtils.isEmpty(input)) {
            mCtrl.getBooks()
                    .subscribe(
                            (book) -> mAdapter.addItem(book.bookName, SelectorAdapter.Type.A),
                            (e) -> e.printStackTrace(),
                            () -> mAdapter.notifyDataSetChanged());

        } else {
            mCtrl.queryEntries(input)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (entry) -> mAdapter.addItem(entry.wordStr, SelectorAdapter.Type.B),
                            (e) -> e.printStackTrace(),
                            () -> mAdapter.notifyDataSetChanged());
        }
    }

    private void onRowClicked(String text) {
        final Intent intent = WordActivity.createIntent(getActivity(), text);
        startActivity(intent);
    }
}
