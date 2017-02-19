package org.zeroxlab.momodict;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.zeroxlab.momodict.ui.FileImportFragment;

public class FileImportActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_fragment);
        setFragments();
    }

    private void setFragments() {
        final FileImportFragment importFrg = new FileImportFragment();
        final FragmentManager mgr = getSupportFragmentManager();
        mgr.beginTransaction()
                .add(R.id.fragment_container, importFrg)
                .commit();
    }
}
