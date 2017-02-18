package org.zeroxlab.momodict.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.zeroxlab.momodict.model.Dictionary;
import org.zeroxlab.momodict.model.Entry;

import java.util.List;

public interface Store {
    boolean addDictionary(@NonNull Dictionary dictionary);

    Dictionary getDictionary(@NonNull String name);

    List<Dictionary> getDictionaries();

    boolean delDictionary(@NonNull String name);

    boolean addEntries(@NonNull List<Entry> entries);

    List<Entry> getEntries(@Nullable String keyWord);

    List<Entry> getEntries(@Nullable String keyWord, @NonNull String dictionaryName);
}
