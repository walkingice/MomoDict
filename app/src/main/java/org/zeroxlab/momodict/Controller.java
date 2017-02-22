package org.zeroxlab.momodict;

import android.content.Context;
import android.support.annotation.NonNull;

import org.zeroxlab.momodict.model.Store;
import org.zeroxlab.momodict.db.realm.RealmStore;
import org.zeroxlab.momodict.model.Card;
import org.zeroxlab.momodict.model.Book;
import org.zeroxlab.momodict.model.Entry;
import org.zeroxlab.momodict.model.Record;

import java.util.Collections;
import java.util.List;

public class Controller {

    private Context mCtx;
    private Store mStore;

    public Controller(@NonNull Context ctx) {
        mCtx = ctx;
        mStore = new RealmStore(mCtx);
    }

    public List<Book> getDictionaries() {
        return mStore.getBooks();
    }

    public List<Entry> getEntries(String keyWord) {
        List<Entry> list = mStore.queryEntries(keyWord);
        Collections.sort(list, (left, right) -> {
            return left.wordStr.indexOf(keyWord) - right.wordStr.indexOf(keyWord);
        });
        return list;
    }

    public List<Record> getRecords() {
        List<Record> records = mStore.getRecords();
        Collections.sort(records, (left, right) -> {
            // sorting by time. Move latest one to head
            return left.time.before(right.time) ? 1 : -1;
        });
        return records;
    }

    public boolean setRecord(@NonNull Record record) {
        return mStore.upsertRecord(record);
    }

    public boolean removeRecord(@NonNull String keyWord) {
        return mStore.removeRecords(keyWord);
    }

    public List<Card> getCards() {
        List<Card> cards = mStore.getCards();
        Collections.sort(cards, (left, right) -> {
            // sorting by time. Move latest one to head
            return left.time.before(right.time) ? 1 : -1;
        });
        return cards;
    }

    public boolean setCard(@NonNull Card card) {
        return mStore.upsertCard(card);
    }

    public boolean removeCards(@NonNull String keyWord) {
        return mStore.removeCards(keyWord);
    }
}
