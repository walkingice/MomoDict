package org.zeroxlab.momodict;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import org.zeroxlab.momodict.reader.Reader;

import java.io.File;

public class FileImportActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mText;

    private boolean mExists = false;

    private static final int REQ_CODE_READ_EXTERNAL = 0x42;

    private static final String sEXT = Environment.getExternalStorageDirectory().getPath();
    private static final String sPATH = sEXT + "/test-dict.tar.bz2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_import);
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission();
    }

    private void initViews() {
        mButton = (Button) findViewById(R.id.btn_1);
        mText = (TextView) findViewById(R.id.text_1);

        File dict = new File(sPATH);
        mExists = dict.exists();
        mText.setText(mExists
                ? String.format("Using file: %s", sPATH)
                : String.format("File %s not exists", sPATH));
        mButton.setEnabled(mExists);

        mButton.setOnClickListener((v) -> {
            onImportButtonClicked();
        });
    }

    private void checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!(readPermission == PackageManager.PERMISSION_GRANTED)) {
            mButton.setEnabled(false);
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_CODE_READ_EXTERNAL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] response) {
        if (reqCode == REQ_CODE_READ_EXTERNAL
                && response[0] == PackageManager.PERMISSION_GRANTED) {
            mButton.setEnabled(mExists);
        }
    }

    private void onImportButtonClicked() {
        //Observable.just(0)
        //        .subscribeOn(Schedulers.io())
        //        .concatMap((i) -> {
        //            return Observable.just(i);
        //        })
        //        .observeOn(MainThread.this)
        mButton.setEnabled(false);
        mText.setText("Importing.....");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Reader reader = new Reader(getCacheDir().getPath(), sPATH);
                reader.parse(FileImportActivity.this);
                Intent intent = new Intent();
                //mButton.setEnabled(true);
                //mText.setText("Imported");
                intent.setData(Uri.parse(sPATH));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }
}
