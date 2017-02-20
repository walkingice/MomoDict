package org.zeroxlab.momodict.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Record;

import java.util.List;

public interface Store {

    int MAX_LENGTH = 1000;

    boolean addDictionary(@NonNull Dictionary dictionary);

    Dictionary getDictionary(@NonNull String name);

    List<Dictionary> getDictionaries();

    boolean delDictionary(@NonNull String name);

    boolean addEntries(@NonNull List<Entry> entries);

    List<Entry> queryEntries(@Nullable String keyWord);

    List<Entry> queryEntries(@Nullable String keyWord, @NonNull String dictionaryName);

    List<Entry> getEntries(@Nullable String keyWord);

    boolean setRecord(@NonNull Record record);

    List<Record> getRecords();
}
