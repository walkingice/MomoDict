package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.zeroxlab.momodict.ui.InputSearchFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = Momodict.TAG;

    static final int REQ_CODE_IMPORT = 0x1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_fragment);
        initView();
        setFragments();
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

    private void setFragments() {
        final InputSearchFragment inputFrg = new InputSearchFragment();
        final FragmentManager mgr = getSupportFragmentManager();
        mgr.beginTransaction()
                .add(R.id.fragment_container, inputFrg)
                .commit();
    }

    private void initView() {
        initActionBar();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
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
}
