package org.zeroxlab.momodict.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public interface Store {

    int MAX_LENGTH = 1000;

    boolean addBook(@NonNull Book book);

    Book getBook(@NonNull String name);

    List<Book> getBooks();

    boolean removeBook(@NonNull String name);

    boolean addEntries(@NonNull List<Entry> entries);

    List<Entry> queryEntries(@Nullable String keyWord);

    List<Entry> queryEntries(@Nullable String keyWord, @NonNull String bookName);

    List<Entry> getEntries(@Nullable String keyWord);

    boolean upsertRecord(@NonNull Record record);

    boolean removeRecords(@NonNull String keyWord);

    List<Record> getRecords();

    boolean upsertCard(@NonNull Card card);

    boolean removeCards(@NonNull String keyWord);

    List<Card> getCards();
}
