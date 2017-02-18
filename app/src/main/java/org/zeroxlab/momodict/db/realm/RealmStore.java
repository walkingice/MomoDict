package org.zeroxlab.momodict.db.realm;

import android.content.Context;
import android.text.TextUtils;

import org.zeroxlab.momodict.db.Store;
import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmStore implements Store {

    private final Context mCtx;

    public RealmStore(Context ctx) {
        mCtx = ctx;
        Realm.init(mCtx);
    }

    @Override
    public boolean addDictionary(Dictionary dictionary) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmDictionary managedDic =
                realm.createObject(RealmDictionary.class, dictionary.bookName);
        managedDic.author = dictionary.author;
        managedDic.wordCount = dictionary.wordCount;
        managedDic.date = dictionary.date;
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public Dictionary getDictionary(String name) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<Dictionary> getDictionaries() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmDictionary> results =
                realm.where(RealmDictionary.class).findAll();
        final List<Dictionary> dics = new ArrayList<>();
        for (RealmDictionary managedDic : results) {
            Dictionary dic = new Dictionary();
            dic.bookName = managedDic.bookName;
            dic.author = managedDic.author;
            dic.wordCount = managedDic.wordCount;
            dic.date = managedDic.date;
            dics.add(dic);
        }
        realm.close();
        return dics;
    }

    @Override
    public boolean delDictionary(String name) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public boolean addEntries(List<Entry> entries) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (Entry entry : entries) {
            RealmEntry managedEntry = realm.createObject(RealmEntry.class);
            managedEntry.wordStr = entry.wordStr;
            managedEntry.source = entry.source;
            managedEntry.data = entry.data;
        }
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public List<Entry> getEntries(String keyWord) {
        final Realm realm = Realm.getDefaultInstance();
        final List<Entry> entries = new ArrayList<>();
        final RealmResults<RealmEntry> managedEntries = TextUtils.isEmpty(keyWord)
                ? realm.where(RealmEntry.class).findAll()
                : realm.where(RealmEntry.class).contains("wordStr", keyWord).findAll();

        for (RealmEntry managedEntry : managedEntries) {
            Entry entry = new Entry();
            entry.source = managedEntry.source;
            entry.wordStr = managedEntry.wordStr;
            entry.data = managedEntry.data;
            entries.add(entry);
        }

        realm.close();
        return entries;
    }

    @Override
    public List<Entry> getEntries(String keyWord, String dictionaryName) {
        throw new RuntimeException("Not implemented yet");
    }
}
