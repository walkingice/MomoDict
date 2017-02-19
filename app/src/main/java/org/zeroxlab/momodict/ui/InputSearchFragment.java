package org.zeroxlab.momodict.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.zeroxlab.momodict.Controller;
import org.zeroxlab.momodict.Momodict;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.WordActivity;
import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.widget.DictionaryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputSearchFragment extends Fragment {

    public static final String TAG = Momodict.TAG;

    private TextView mText;
    private RecyclerView mList;
    private SelectorAdapter mAdapter;
    private EditText mInput;
    private Controller mCtrl;
    private View mDel;

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);

        mCtrl = new Controller(getActivity());

        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new DictionaryRowPresenter());
        map.put(SelectorAdapter.Type.B, new WordRowPresenter((view) -> {
            onRowClicked((String) view.getTag());
        }));
        mAdapter = new SelectorAdapter(map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View fragmentView = inflater.inflate(R.layout.fragment_input_search, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareDictionary();
        onUpdateList();
    }

    private void prepareDictionary() {
        List<Dictionary> dics = mCtrl.getDictionaries();
        mText.setText("Dictionary num:" + dics.size());
        mInput.setEnabled(dics.size() > 0);
    }


    private void initViews(View fv) {
        mText = (TextView) fv.findViewById(R.id.text_1);
        mList = (RecyclerView) fv.findViewById(R.id.list);
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

        mList.setAdapter(mAdapter);
        mDel = fv.findViewById(R.id.btn_1);
        mDel.setOnClickListener((v) -> clearInput());
    }

    private void clearInput() {
        mInput.setText("");
    }

    private void onUpdateList() {
        String input = mInput.getText().toString();
        Log.d(TAG, "Input: " + input);
        mAdapter.clear();
        if (TextUtils.isEmpty(input)) {
            List<Dictionary> dics = mCtrl.getDictionaries();
            for (Dictionary d : dics) {
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
        Intent intent = WordActivity.createIntent(getActivity(), text);
        startActivity(intent);
    }
}
