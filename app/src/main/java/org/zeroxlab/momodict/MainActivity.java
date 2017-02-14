package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Momodict";

    static final int REQ_CODE_IMPORT = 0x1002;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.btn_1);
        mButton.setOnClickListener((v) -> {
            Intent i = new Intent();
            i.setClass(this, FileImportActivity.class);
            startActivityForResult(i, REQ_CODE_IMPORT);
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case REQ_CODE_IMPORT:
                onResultImport(resultCode, data);
                break;
        }
    }

    private void onResultImport(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "Imported from file " + uri.getPath());
        }
    }
}
