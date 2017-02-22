package org.zeroxlab.momodict.db.realm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Card;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Record;
import org.zeroxlab.momodict.model.Store;

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
    public boolean addBook(Book book) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmBook managedDic =
                realm.createObject(RealmBook.class, book.bookName);
        managedDic.author = book.author;
        managedDic.wordCount = book.wordCount;
        managedDic.date = book.date;
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public Book getBook(String name) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<Book> getBooks() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmBook> results =
                realm.where(RealmBook.class).findAll();
        final List<Book> dics = new ArrayList<>();
        for (RealmBook managedDic : results) {
            Book dic = new Book();
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
    public boolean removeBook(String name) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public boolean addEntries(List<Entry> entries) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (Entry entry : entries) {
            RealmEntry managedEntry = realm.createObject(RealmEntry.class);
            managedEntry.wordStr = entry.wordStr;
            managedEntry.sourceBook = entry.source;
            managedEntry.data = entry.data;
        }
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public List<Entry> queryEntries(String keyWord) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmEntry> managedEntries = TextUtils.isEmpty(keyWord)
                ? realm.where(RealmEntry.class).findAll()
                : realm.where(RealmEntry.class).contains("wordStr", keyWord).findAll();

        List<Entry> mapped = map(managedEntries);
        realm.close();
        return mapped;
    }

    @Override
    public List<Entry> getEntries(String keyWord) {
        final Realm realm = Realm.getDefaultInstance();
        if (TextUtils.isEmpty(keyWord)) {
            throw new RuntimeException("Keyword is empty");
        }
        final RealmResults<RealmEntry> managedEntries = realm
                .where(RealmEntry.class)
                .equalTo("wordStr", keyWord)
                .findAll();

        List<Entry> mapped = map(managedEntries);
        realm.close();
        return mapped;
    }

    @Override
    public boolean upsertRecord(@NonNull Record record) {
        final Realm realm = Realm.getDefaultInstance();
        RealmRecord previous = realm.where(RealmRecord.class)
                .equalTo("wordStr", record.wordStr)
                .findFirst();
        realm.beginTransaction();
        RealmRecord managedRecord = (previous == null)
                ? realm.createObject(RealmRecord.class, record.wordStr)
                : previous;
        managedRecord.count = record.count;
        managedRecord.time = record.time;
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public List<Record> getRecords() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRecord> managedRecords = realm
                .where(RealmRecord.class)
                .findAll();

        final List<Record> records = new ArrayList<>();
        for (int i = 0; i < managedRecords.size(); i++) {
            RealmRecord managedRecord = managedRecords.get(i);
            Record record = new Record();
            record.count = managedRecord.count;
            record.wordStr = managedRecord.wordStr;
            record.time = managedRecord.time;
            records.add(record);
        }
        realm.close();
        return records;
    }

    @Override
    public boolean upsertCard(@NonNull Card card) {
        final Realm realm = Realm.getDefaultInstance();
        RealmCard previous = realm.where(RealmCard.class)
                .equalTo("wordStr", card.wordStr)
                .findFirst();
        realm.beginTransaction();
        RealmCard managedCard = (previous == null)
                ? realm.createObject(RealmCard.class, card.wordStr)
                : previous;
        managedCard.time = card.time;
        managedCard.note = card.note;
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public boolean removeCards(@NonNull String keyWord) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<RealmCard> rows = realm.where(RealmCard.class)
                .equalTo("wordStr", keyWord)
                .findAll();
        rows.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public List<Card> getCards() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmCard> managedCards = realm
                .where(RealmCard.class)
                .findAll();

        final List<Card> cards = new ArrayList<>();
        for (int i = 0; i < managedCards.size(); i++) {
            RealmCard managedCard = managedCards.get(i);
            Card card = new Card();
            card.wordStr = managedCard.wordStr;
            card.note = managedCard.note;
            card.time = managedCard.time;
            cards.add(card);
        }
        realm.close();
        return cards;
    }

    @Override
    public boolean removeRecords(String keyWord) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<RealmRecord> rows = realm.where(RealmRecord.class)
                .equalTo("wordStr", keyWord)
                .findAll();
        rows.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
        return true;
    }

    @Override
    public List<Entry> queryEntries(String keyWord, String bookName) {
        throw new RuntimeException("Not implemented yet");
    }

    private List<Entry> map(RealmResults<RealmEntry> managedEntries) {
        final List<Entry> entries = new ArrayList<>();
        for (int i = 0; entries.size() < MAX_LENGTH && i < managedEntries.size(); i++) {
            RealmEntry managedEntry = managedEntries.get(i);
            Entry entry = new Entry();
            entry.source = managedEntry.sourceBook;
            entry.wordStr = managedEntry.wordStr;
            entry.data = managedEntry.data;
            entries.add(entry);
        }
        return entries;
    }
}
