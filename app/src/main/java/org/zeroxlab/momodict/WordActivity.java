package org.zeroxlab.momodict;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.zeroxlab.momodict.db.Store;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Entry;

import java.util.List;

public class WordActivity extends AppCompatActivity {

    private TextView mText;

    public static Intent createIntent(Context ctx, String text) {
        Intent intent = new Intent(ctx, WordActivity.class);
        intent.putExtra(Momodict.EXTRA_DATA_1, text);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        initViews();

        String text = getIntent().getStringExtra(Momodict.EXTRA_DATA_1);
        onDisplayDetail(text);
    }

    private void initViews() {
        mText = (TextView) findViewById(R.id.text_1);
    }

    private void onDisplayDetail(String target) {
        final StringBuilder sb = new StringBuilder();
        Store store = new RealmStore(this);
        List<Entry> entries = store.getEntries(target);
        for (Entry entry : entries) {
            sb.append(entry.data);
        }
        mText.setText(sb.toString());
    }
}
