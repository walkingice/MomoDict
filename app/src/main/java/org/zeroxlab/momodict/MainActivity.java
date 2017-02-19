package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.widget.DictionaryRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = Momodict.TAG;

    static final int REQ_CODE_IMPORT = 0x1002;

    private TextView mText;
    private RecyclerView mList;
    private SelectorAdapter mAdapter;
    private EditText mInput;
    private Controller mCtrl;
    private View mDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCtrl = new Controller(this);
        initView();

        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new DictionaryRowPresenter());
        map.put(SelectorAdapter.Type.B, new WordRowPresenter((view) -> {
            onRowClicked((String) view.getTag());
        }));
        mAdapter = new SelectorAdapter(map);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareDictionary();
        onUpdateList();
    }

    private void initView() {
        initActionBar();
        mText = (TextView) findViewById(R.id.text_1);
        mList = (RecyclerView) findViewById(R.id.list);
        mInput = (EditText) findViewById(R.id.input_1);
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

        mDel = findViewById(R.id.btn_1);
        mDel.setOnClickListener((v) -> clearInput());
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.menu_import, Menu.NONE, "Import dictionary");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import:
                onImportClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case REQ_CODE_IMPORT:
                onResultImport(resultCode, data);
                break;
        }
    }

    private void onImportClicked() {
        Intent i = new Intent();
        i.setClass(this, FileImportActivity.class);
        startActivityForResult(i, REQ_CODE_IMPORT);
    }

    private void onResultImport(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "Imported from file " + uri.getPath());
        }
    }

    private void prepareDictionary() {
        List<Dictionary> dics = mCtrl.getDictionaries();
        mText.setText("Dictionary num:" + dics.size());
        mInput.setEnabled(dics.size() > 0);
    }

    private void onRowClicked(String text) {
        Intent intent = WordActivity.createIntent(this, text);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mInput.getText())) {
            super.onBackPressed();
        } else {
            clearInput();
        }
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
}
