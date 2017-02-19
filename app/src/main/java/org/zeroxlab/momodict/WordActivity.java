package org.zeroxlab.momodict;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.zeroxlab.momodict.ui.WordFragment;

public class WordActivity extends AppCompatActivity {

    public static Intent createIntent(Context ctx, String text) {
        Intent intent = new Intent(ctx, WordActivity.class);
        intent.putExtra(Momodict.EXTRA_DATA_1, text);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_one_fragment);
        setFragments();
    }

    private void setFragments() {
        final WordFragment wordFrg = new WordFragment();
        final FragmentManager mgr = getSupportFragmentManager();
        mgr.beginTransaction()
                .add(R.id.fragment_container, wordFrg)
                .commit();
    }
}
