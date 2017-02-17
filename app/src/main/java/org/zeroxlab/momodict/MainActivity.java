package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.zeroxlab.momodict.db.realm.Dictionary;
import org.zeroxlab.momodict.db.realm.WordEntry;
import org.zeroxlab.momodict.widget.SelectorAdapter;
import org.zeroxlab.momodict.widget.WordRowPresenter;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = Momodict.TAG;

    static final int REQ_CODE_IMPORT = 0x1002;

    private TextView mText;
    private RecyclerView mList;
    private SelectorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Realm.init(this);

        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new WordRowPresenter((view) -> {
            onRowClicked((String) view.getTag());
        }));
        mAdapter = new SelectorAdapter(map);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareDictionary();
    }

    private void initView() {
        initActionBar();
        mText = (TextView) findViewById(R.id.text_1);
        mList = (RecyclerView) findViewById(R.id.list);
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

    // TODO: move Realm to another layer
    private void prepareDictionary() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Dictionary> dics = realm.where(Dictionary.class).findAll();
        mText.setText("Dictionary num:" + dics.size());

        if (dics.size() != 0) {
            Dictionary d = dics.get(0);
            for (WordEntry entry : d.words) {
                mAdapter.addItem(entry.wordStr, SelectorAdapter.Type.A);
            }
            mAdapter.notifyDataSetChanged();
        }
        realm.close();
    }

    private void onRowClicked(String text) {
        Intent intent = WordActivity.createIntent(this, text);
        startActivity(intent);
    }
}
