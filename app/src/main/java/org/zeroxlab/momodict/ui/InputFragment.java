package org.zeroxlab.momodict.ui;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.zeroxlab.momodict.Controller;
import org.zeroxlab.momodict.Momodict;
import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.WordActivity;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.widget.BackKeyHandler;
import org.zeroxlab.momodict.widget.DictionaryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.ViewPagerFocusable;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Fragment to provide UI which user can input a text to query, and display a list for queried text.
 */
public class InputFragment extends Fragment implements BackKeyHandler, ViewPagerFocusable {

    private static final String TAG = Momodict.TAG;
    private static final int INPUT_DELAY = 300;

    private SelectorAdapter mAdapter;
    private EditText mInput;
    private Controller mCtrl;

    /**
     * User input won't be send to mCtrl directly. Instead, send to here so we have more flexibility
     * to use mCtrl.
     */
    private Subject<String, String> mQuery;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        mCtrl = new Controller(getActivity());

        // create adapter for RecyclerView. Adapter handles two kinds of row, one for "dictionary"
        // and another for "word".
        final Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new DictionaryRowPresenter());
        map.put(SelectorAdapter.Type.B,
                new WordRowPresenter((view) -> onRowClicked((String) view.getTag())));
        mAdapter = new SelectorAdapter(map);

        // This fragment might be destroy if user scroll to third Tab, so we have to re-create it
        // in onCreate callback.
        mQuery = PublishSubject.create();
        // If user type quickly, do not query until user stop inputting.
        mQuery.debounce(INPUT_DELAY, TimeUnit.MILLISECONDS)
                .concatMap((input) -> mCtrl.queryEntries(input).toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((list) -> {
                    mAdapter.clear();
                    for (Entry entry : list) {
                        mAdapter.addItem(entry.wordStr, SelectorAdapter.Type.B);
                    }
                    mAdapter.notifyDataSetChanged();
                }, (e) -> e.printStackTrace());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mQuery.onCompleted();
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

    /**
     * Callback for {@link BackKeyHandler}. Called when user clicked back key.
     * If the input field is not empty, back-key-event will clear the field.
     * We regards this as "handled"
     *
     * @return true if this fragment handled back-key-event
     */
    @Override
    public boolean backKeyHandled() {
        if (TextUtils.isEmpty(mInput.getText())) {
            return false;
        } else {
            clearInput();
            return true;
        }
    }

    /**
     * Callback for {@link ViewPagerFocusable}. Called when ViewPager focused this fragment
     */
    @Override
    public void onViewPagerFocused() {
        // if this page becomes visible, show soft-keyboard
        mInput.requestFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
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

    /**
     * To update status of Input view - disable or enable it.
     */
    private void onUpdateInput() {
        // If there is no any available dictionary, disable Input view.
        mCtrl.getBooks()
                .count()
                .subscribe((count) -> {
                    mInput.setEnabled(count > 0);
                    if (count > 0 && !TextUtils.isEmpty(mInput.getText())) {
                        mInput.selectAll();
                    }
                });
    }

    /**
     * Update list according to user's input.
     */
    private void onUpdateList() {
        final String input = mInput.getText().toString().trim();
        Log.d(TAG, "Input: " + input);
        mAdapter.clear();
        if (TextUtils.isEmpty(input)) {
            // User haven't input anything, just clear the list
            mAdapter.notifyDataSetChanged();

        } else {
            mQuery.onNext(input);
        }
    }

    /**
     * Callback for user clicked any queried word.
     *
     * @param text the text of the row which user clicked
     */
    private void onRowClicked(String text) {
        final Intent intent = WordActivity.createIntent(getActivity(), text);
        startActivity(intent);
    }
}
