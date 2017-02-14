package org.zeroxlab.momodict;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class FileImportActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mText;

    private static final String sEXT = Environment.getExternalStorageDirectory().getPath();
    private static final String sPATH = sEXT + "/test-dict.tar.bz2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_import);
        initViews();
    }

    private void initViews() {
        mButton = (Button) findViewById(R.id.btn_1);
        mText = (TextView) findViewById(R.id.text_1);


        File dict = new File(sPATH);
        mText.setText(dict.exists()
                ? String.format("Using file: %s", sPATH)
                : String.format("File %s not exists", sPATH));
        mButton.setEnabled(dict.exists());

        mButton.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse(sPATH));
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }
}
