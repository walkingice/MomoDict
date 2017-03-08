package org.zeroxlab.momodict;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import org.zeroxlab.momodict.ui.FileImportFragment;
import org.zeroxlab.momodict.ui.FilePickerFragment;
import org.zeroxlab.momodict.ui.FragmentListener;

public class FileImportActivity extends AppCompatActivity implements FragmentListener {

    private static final String sEXTDIR = Environment.getExternalStorageDirectory().getPath();
    private static final String sEXT = ".tar.bz2";

    private final static String TAG_IMPORT_FILE = "fragment_to_import_file";
    private final static String TAG_PICK_FILE = "fragment_to_pick_file";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        setFragments();
    }

    private void setFragments() {
        final FileImportFragment importFrg = FileImportFragment.newInstance(sEXTDIR);
        final FragmentManager mgr = getSupportFragmentManager();
        mgr.beginTransaction()
                .add(R.id.fragment_container, importFrg, TAG_IMPORT_FILE)
                .commit();
    }

    @Override
    public void onNotified(@Nullable Fragment from, @NonNull TYPE type, @Nullable Object payload) {
        if (type == TYPE.POP_FRAGMENT) {
            popFragment();
        } else if (from instanceof FileImportFragment) {
            if (type == TYPE.VIEW_ACTION && payload.equals(FileImportFragment.PICK_A_FILE)) {
                openFilePicker();
            }
        }
    }

    private void openFilePicker() {
        final FragmentManager mgr = getSupportFragmentManager();
        mgr.beginTransaction()
                .replace(R.id.fragment_container,
                        FilePickerFragment.newInstance(sEXTDIR, sEXT),
                        TAG_PICK_FILE)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_PICK_FILE);
        if (f == null) {
            popFragment();
        } else {
            boolean handled = ((FilePickerFragment) f).goParentDirectory();
            if (!handled) {
                popFragment();
            }
        }
    }

    private void popFragment() {
        // FIXME: so stupid implementation
        Fragment fragPick = getSupportFragmentManager().findFragmentByTag(TAG_PICK_FILE);
        Fragment fragImport = getSupportFragmentManager().findFragmentByTag(TAG_IMPORT_FILE);
        if (fragPick != null && fragImport != null) {
            String chosen = fragPick.getArguments().getString(FilePickerFragment.ARG_PATH);
            if (!TextUtils.isEmpty(chosen)) {
                fragImport.getArguments().putString(FileImportFragment.ARG_PATH, chosen);
            }
        }

        super.onBackPressed();
    }
}
