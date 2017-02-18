package org.zeroxlab.momodict;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.zeroxlab.momodict.db.realm.RealmDictionary;
import org.zeroxlab.momodict.db.realm.RealmEntry;

import io.realm.Realm;
import io.realm.RealmResults;

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
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmDictionary> dics = realm.where(RealmDictionary.class).findAll();
        final StringBuilder sb = new StringBuilder();
        // STUPID!
        for (RealmDictionary d : dics) {
            for (RealmEntry entry : d.words) {
                if (entry.wordStr.equals(target)) {
                    sb.append(entry.data);
                    sb.append("\n\n\n");
                }
            }
        }
        realm.close();

        mText.setText(sb.toString());
    }
}
