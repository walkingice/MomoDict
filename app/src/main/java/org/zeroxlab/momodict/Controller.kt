package org.zeroxlab.momodict

import android.content.Context
import androidx.room.Room.databaseBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeroxlab.momodict.db.room.RoomStore
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.model.Store
import rx.Observable

// FIXME: should avoid main thread
class Controller @JvmOverloads constructor(
    private val mCtx: Context,
    private val mStore: Store = databaseBuilder(
        mCtx.applicationContext,
        RoomStore::class.java,
        RoomStore.DB_NAME
    )
        .allowMainThreadQueries()
        .build()
) {

    // sorting by time. Move latest one to head
    private val recordTimeComparator: Comparator<Record> = Comparator { left, right ->
        if (left.time!!.before(right.time)) 1 else -1
    }

    // sorting by time. Move latest one to head
    private val cardTimeComparator = Comparator<Card> { left, right ->
        if (left.time!!.before(right.time)) 1 else -1
    }

    fun getBooks(scope: CoroutineScope, cb: (List<Book>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val books = mStore.getBooks()
            withContext(scope.coroutineContext) {
                cb(books)
            }
        }
    }

    fun removeBook(bookName: String): Boolean {
        return mStore.removeBook(bookName)
    }

    fun queryEntries(
        scope: CoroutineScope,
        keyWord: String,
        cb: (List<Entry>) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            // to make sure exact matched words are returned
            val exact = syncGetEntries(keyWord)
            val list = mStore.queryEntries(keyWord)
            val comparator = Comparator<Entry> { left, right ->
                left.wordStr!!.indexOf(keyWord) - right.wordStr!!.indexOf(keyWord)
            }

            list.sortWith(comparator)
            exact.forEach { list.add(0, it) }
            val distinct = list.distinctBy { item -> item.wordStr }
            withContext(scope.coroutineContext) {
                cb.invoke(distinct)
            }
        }
    }

    fun getEntries(scope: CoroutineScope, keyWord: String, cb: (List<Entry>) -> Unit) {
        scope.launch(Dispatchers.IO) {
            // to make sure exact matched words are returned
            val exact = syncGetEntries(keyWord)
            withContext(scope.coroutineContext) {
                cb(exact)
            }
        }
    }

    fun getRecords(): Observable<Record> {
        val records = mStore.getRecords().apply { sortWith(recordTimeComparator) }
        return Observable.from(records)
    }

    fun clearRecords() {
        getRecords().subscribe { record -> mStore.removeRecords(record.wordStr!!) }
    }

    fun setRecord(record: Record): Boolean {
        return mStore.upsertRecord(record)
    }

    fun removeRecord(keyWord: String): Boolean {
        return mStore.removeRecords(keyWord)
    }

    fun getCards(): Observable<Card> {
        val cards = mStore.getCards()
        cards.sortWith(cardTimeComparator)

        return Observable.from(cards)
    }

    fun setCard(card: Card): Boolean {
        return mStore.upsertCard(card)
    }

    fun removeCards(keyWord: String): Boolean {
        return mStore.removeCards(keyWord)
    }

    private fun syncGetEntries(keyWord: String): List<Entry> {
        val list = mStore.getEntries(keyWord)
        val comparator = Comparator<Entry> { left, right ->
            left.wordStr!!.indexOf(keyWord) - right.wordStr!!.indexOf(keyWord)
        }
        list.sortWith(comparator)
        return list
    }
}
