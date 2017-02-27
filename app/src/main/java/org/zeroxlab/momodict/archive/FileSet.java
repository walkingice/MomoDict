package org.zeroxlab.momodict.archive;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;

import static org.zeroxlab.momodict.archive.FileSet.Type.DICT;
import static org.zeroxlab.momodict.archive.FileSet.Type.IDX;
import static org.zeroxlab.momodict.archive.FileSet.Type.IFO;

/**
 * A structure for extracted files path.
 */
public class FileSet {

    private Map<Type, String> mPaths = new HashMap<>();
    private Action0 mCleanCallback;

    public enum Type {
        IFO,
        IDX,
        DICT,
    }

    /**
     * For a dictionary, to check every necessary file exists.
     * @return true if necessary files exist.
     */
    public boolean isSane() {
        return has(IFO) && has(IDX) && has(DICT);
    }

    public boolean has(Type type) {
        return mPaths.containsKey(type);
    }

    public void set(Type type, String path) {
        mPaths.put(type, path);
    }

    public String get(Type type) {
        return mPaths.get(type);
    }

    public void setCleanCallback(Action0 cb) {
        mCleanCallback = cb;
    }

    /**
     * Call this to clean extracted files.
     */
    public void clean() {
        if (mCleanCallback != null) {
            mCleanCallback.call();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Type t : Type.values()) {
            sb.append(String.format("Type: %d, path: ", t, mPaths.get(t)));
        }
        return sb.toString();
    }
}
