package org.zeroxlab.momodict.archive;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;

import static org.zeroxlab.momodict.archive.DictionaryArchive.Type.DICT;
import static org.zeroxlab.momodict.archive.DictionaryArchive.Type.IDX;
import static org.zeroxlab.momodict.archive.DictionaryArchive.Type.IFO;

public class DictionaryArchive {

    private Map<Type, String> mPaths = new HashMap<>();
    private Action0 mCleanCallback;

    public enum Type {
        IFO,
        IDX,
        DICT,
    }

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
