package org.zeroxlab.momodict.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.reader.Reader;

import java.io.File;

public class FileImportFragment extends Fragment {

    private static final int REQ_CODE_READ_EXTERNAL = 0x42;
    private static final String sEXT = Environment.getExternalStorageDirectory().getPath();
    private static final String sPATH = sEXT + "/test-dict.tar.bz2";

    private Button mButton;
    private TextView mText;

    private boolean mExists = false;

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View fragmentView = inflater.inflate(R.layout.fragment_file_import, container, false);
        initViews(fragmentView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] response) {
        if (reqCode == REQ_CODE_READ_EXTERNAL
                && response[0] == PackageManager.PERMISSION_GRANTED) {
            mButton.setEnabled(mExists);
        }
    }

    private void checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!(readPermission == PackageManager.PERMISSION_GRANTED)) {
            mButton.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_CODE_READ_EXTERNAL);
        }
    }

    private void initViews(View fv) {
        mButton = (Button) fv.findViewById(R.id.btn_1);
        mText = (TextView) fv.findViewById(R.id.text_1);

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

    private void onImportButtonClicked() {
        //Observable.just(0)
        //        .subscribeOn(Schedulers.io())
        //        .concatMap((i) -> {
        //            return Observable.just(i);
        //        })
        //        .observeOn(MainThread.this)
        mButton.setEnabled(false);
        mText.setText("Importing.....");
        Runnable runnable = () -> {
            Reader reader = new Reader(getActivity().getCacheDir().getPath(), sPATH);
            reader.parse(getActivity());
            Intent intent = new Intent();
            intent.setData(Uri.parse(sPATH));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().runOnUiThread(() -> mText.setText("Imported"));
            getActivity().finish();
        };
        Thread t = new Thread(runnable);
        t.start();
    }
}
