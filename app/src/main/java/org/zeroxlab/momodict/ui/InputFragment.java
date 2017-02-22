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
import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.widget.BackKeyHandler;
import org.zeroxlab.momodict.widget.DictionaryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final List<Book> books = mCtrl.getBooks();
        mInput.setEnabled(books.size() > 0);
        if (books.size() > 0 && !TextUtils.isEmpty(mInput.getText())) {
            mInput.selectAll();
        }
    }

    private void onUpdateList() {
        final String input = mInput.getText().toString();
        Log.d(TAG, "Input: " + input);
        mAdapter.clear();
        if (TextUtils.isEmpty(input)) {
            List<Book> books = mCtrl.getBooks();
            for (Book d : books) {
                mAdapter.addItem(d.bookName, SelectorAdapter.Type.A);
            }
        } else {
            List<Entry> entries = mCtrl.getEntries(input);
            for (Entry entry : entries) {
                mAdapter.addItem(entry.wordStr, SelectorAdapter.Type.B);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    private void onRowClicked(String text) {
        final Intent intent = WordActivity.createIntent(getActivity(), text);
        startActivity(intent);
    }
}
