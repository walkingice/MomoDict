package org.zeroxlab.momodict;

import android.content.Context;
import android.support.annotation.NonNull;

import org.zeroxlab.momodict.db.Store;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;

import java.util.Collections;
import java.util.List;

public class Controller {

    private Context mCtx;
    private Store mStore;

    public Controller(@NonNull Context ctx) {
        mCtx = ctx;
        mStore = new RealmStore(mCtx);
    }

    public List<Dictionary> getDictionaries() {
        return mStore.getDictionaries();
    }

    public List<Entry> getEntries(String keyWord) {
        List<Entry> list = mStore.getEntries(keyWord);
        Collections.sort(list, (left, right) -> {
            return left.wordStr.indexOf(keyWord) - right.wordStr.indexOf(keyWord);
        });
        return list;
    }
}
