package org.zeroxlab.momodict.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.zeroxlab.momodict.R;
import org.zeroxlab.momodict.widget.FileRowPresenter;
import org.zeroxlab.momodict.widget.SelectorAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePickerFragment extends Fragment {

    public final static String ARG_PATH = "path_to_open";
    public final static String ARG_EXTENSION = "filename_extension";
    private RecyclerView mList;
    private SelectorAdapter mAdapter;
    private Button mBtnChoose;
    private Button mBtnCancel;
    private TextView mCurrentPathView;
    private String mChosen;
    private String mCurrentPath;
    private String mExtension;

    @SuppressWarnings("unused")
    public static FilePickerFragment newInstance(@NonNull String path,
                                                 @NonNull String extension) {
        FilePickerFragment fragment = new FilePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putString(ARG_EXTENSION, extension);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurrentPath = args.getString(ARG_PATH);
        mExtension = args.getString(ARG_EXTENSION, "");

        // check
        File check = new File(mCurrentPath);
        if (!check.exists() && !check.canRead()) {
            throw new RuntimeException("Cannot open path:" + mCurrentPath);
        }
        if (check.isFile()) {
            mCurrentPath = check.getParentFile().getPath();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_picker, container, false);
        initViews(view);
        Map<SelectorAdapter.Type, SelectorAdapter.Presenter> map = new HashMap<>();
        map.put(SelectorAdapter.Type.A, new FileRowPresenter(getContext(), (v) -> onFileClicked(v)));
        mAdapter = new SelectorAdapter(map);
        mList.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(mCurrentPath);
    }

    private void initViews(View container) {
        mList = (RecyclerView) container.findViewById(R.id.list);
        LinearLayoutManager mgr = (LinearLayoutManager) mList.getLayoutManager();
        mList.addItemDecoration(new DividerItemDecoration(getContext(), mgr.getOrientation()));
        mCurrentPathView = (TextView) container.findViewById(R.id.picker_current_path);
        mBtnCancel = (Button) container.findViewById(R.id.picker_btn_cancel);
        mBtnChoose = (Button) container.findViewById(R.id.picker_btn_choose);
        mBtnChoose.setEnabled(false);

        mBtnChoose.setOnClickListener(view -> {
            if (getActivity() instanceof FragmentListener) {
                getArguments().putString(ARG_PATH, mChosen);
                ((FragmentListener) getActivity()).onNotified(this,
                        FragmentListener.TYPE.POP_FRAGMENT,
                        null);
            }
        });
        mBtnCancel.setOnClickListener(view -> {
            if (getActivity() instanceof FragmentListener) {
                getArguments().remove(ARG_PATH);
                ((FragmentListener) getActivity()).onNotified(this,
                        FragmentListener.TYPE.POP_FRAGMENT,
                        null);
            }
        });
    }

    private void updateList(String path) {
        mCurrentPathView.setText(mCurrentPath);
        mAdapter.clear();
        File f = new File(path);
        if (f.getParentFile() != null) {
            FileRowPresenter.Item parent = new FileRowPresenter.Item("..", f.getParentFile());
            mAdapter.addItem(parent, SelectorAdapter.Type.A);
        }
        File dir = f.isDirectory() ? f : f.getParentFile();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : dir.listFiles()) {
                FileRowPresenter.Item item = new FileRowPresenter.Item(file.getName(), file);
                mAdapter.addItem(item, SelectorAdapter.Type.A);
            }
        }

        mBtnChoose.setEnabled(mChosen != null
                && mChosen.endsWith(mExtension)
                && new File(mChosen).canRead());
        mAdapter.notifyDataSetChanged();
    }

    private void onFileClicked(View v) {
        final File file = (File) v.getTag();
        final String selectedPath = file.getPath();
        if (TextUtils.equals(mChosen, file.getPath())) {
            return;
        }
        if (file.isFile() && selectedPath.endsWith(mExtension)) {
            mChosen = selectedPath;
        } else {
            mCurrentPath = selectedPath;
        }
        updateList(mCurrentPath);
    }

    public boolean goParentDirectory() {
        File current = new File(mCurrentPath);
        File parent = current.getParentFile();
        if (parent == null) {
            return false;
        } else {
            mCurrentPath = parent.getPath();
            updateList(mCurrentPath);
            return true;
        }
    }
}
