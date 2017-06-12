package org.zeroxlab.momodict

import android.content.Context
import org.zeroxlab.momodict.db.realm.RealmStore
import org.zeroxlab.momodict.model.*
import rx.Observable
import java.util.*

class Controller @JvmOverloads constructor(private val mCtx: Context,
                                           private val mStore: Store = RealmStore(mCtx)) {

    val books: Observable<Book>
        get() = Observable.from(mStore.books)

    fun removeBook(bookName: String): Boolean {
        return mStore.removeBook(bookName)
    }

    fun queryEntries(keyWord: String): Observable<Entry> {
        // to make sure exact matched words are returned
        val exact = mStore.getEntries(keyWord)
        val list = mStore.queryEntries(keyWord)
        list.addAll(exact)

        Collections.sort(list) { left, right -> left.wordStr.indexOf(keyWord) - right.wordStr.indexOf(keyWord) }
        return Observable.from(list).distinct { item -> item.wordStr }
    }

    fun getEntries(keyWord: String): Observable<Entry> {
        val list = mStore.getEntries(keyWord)

        Collections.sort(list) { left, right -> left.wordStr.indexOf(keyWord) - right.wordStr.indexOf(keyWord) }
        return Observable.from(list)
    }

    // sorting by time. Move latest one to head
    val records: Observable<Record>
        get() {
            val records = mStore.records
            Collections.sort(records) { left, right -> if (left.time.before(right.time)) 1 else -1 }
            return Observable.from(records)
        }

    fun clearRecords() {
        records.subscribe { record -> mStore.removeRecords(record.wordStr) }
    }

    fun setRecord(record: Record): Boolean {
        return mStore.upsertRecord(record)
    }

    fun removeRecord(keyWord: String): Boolean {
        return mStore.removeRecords(keyWord)
    }

    // sorting by time. Move latest one to head
    val cards: Observable<Card>
        get() {
            val cards = mStore.cards
            Collections.sort(cards) { left, right -> if (left.time.before(right.time)) 1 else -1 }
            return Observable.from(cards)
        }

    fun setCard(card: Card): Boolean {
        return mStore.upsertCard(card)
    }

    fun removeCards(keyWord: String): Boolean {
        return mStore.removeCards(keyWord)
    }
}
