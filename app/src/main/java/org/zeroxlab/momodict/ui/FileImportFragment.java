package org.zeroxlab.momodict.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.reader.Reader;

import java.io.File;

public class FileImportFragment extends Fragment {

    public static final String ARG_PATH = "argument_path";
    public static final String PICK_A_FILE = "to_pick_a_file_to_import";

    private static final int REQ_CODE_READ_EXTERNAL = 0x42;

    private Button mBtnImport;
    private Button mBtnChoose;
    private TextView mText;

    private boolean mExists = false;

    public static FileImportFragment newInstance(String path) {
        FileImportFragment fragment = new FileImportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@NonNull Bundle savedState) {
        super.onCreate(savedState);
        String path = getArguments().getString(ARG_PATH);
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("No file path to import");
        }
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
        File dict = new File(getArguments().getString(ARG_PATH));
        mExists = dict.exists() && dict.isFile();
        mText.setText(mExists
                ? String.format("Using file: %s", dict.getPath())
                : String.format("File %s not exists", dict.getPath()));
        mBtnImport.setEnabled(mExists);
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
            mBtnImport.setEnabled(mExists);
        }
    }

    private void checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!(readPermission == PackageManager.PERMISSION_GRANTED)) {
            mBtnImport.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_CODE_READ_EXTERNAL);
        }
    }

    private void initViews(View fv) {
        mBtnChoose = (Button) fv.findViewById(R.id.btn_1);
        mBtnImport = (Button) fv.findViewById(R.id.btn_2);
        mText = (TextView) fv.findViewById(R.id.text_1);

        mBtnChoose.setOnClickListener((v) -> {
            if (getActivity() instanceof FragmentListener) {
                FragmentListener parent = (FragmentListener) getActivity();
                parent.onNotified(this, FragmentListener.TYPE.VIEW_ACTION, PICK_A_FILE);
            }
        });

        mBtnImport.setOnClickListener((v) -> {
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
        mBtnImport.setEnabled(false);
        mText.setText("Importing.....");
        Runnable runnable = () -> {
            Reader reader = new Reader(getActivity().getCacheDir().getPath(),
                    getArguments().getString(ARG_PATH));
            reader.parse(getActivity());
            Intent intent = new Intent();
            intent.setData(Uri.parse(getArguments().getString(ARG_PATH)));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().runOnUiThread(() -> mText.setText("Imported"));
            getActivity().finish();
        };
        Thread t = new Thread(runnable);
        t.start();
    }
}
