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

// FIXME: should avoid main thread
// TODO: How to handle exception in each function call?
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

    suspend fun queryEntries(
        keyWord: String
    ): List<Entry> = withContext(Dispatchers.IO) {
        // to make sure exact matched words are returned
        val exact = syncGetEntries(keyWord)
        val list = mStore.queryEntries(keyWord)
        val comparator = Comparator<Entry> { left, right ->
            left.wordStr!!.indexOf(keyWord) - right.wordStr!!.indexOf(keyWord)
        }

        list.sortWith(comparator)
        exact.forEach { list.add(0, it) }
        val distinct = list.distinctBy { item -> item.wordStr }
        distinct
    }

    suspend fun getEntries(keyWord: String): List<Entry> = withContext(Dispatchers.IO) {
        // to make sure exact matched words are returned
        syncGetEntries(keyWord)
    }

    suspend fun getRecords(): (List<Record>) = withContext(Dispatchers.IO) {
        mStore.getRecords().apply { sortWith(recordTimeComparator) }
    }

    suspend fun clearRecords() = withContext(Dispatchers.IO) {
        val records = getRecords()
        records.forEach { r ->
            mStore.removeRecords(r.wordStr)
        }
    }

    suspend fun setRecord(record: Record): Boolean = withContext(Dispatchers.IO) {
        mStore.upsertRecord(record)
    }

    suspend fun removeRecord(keyWord: String): Boolean = withContext(Dispatchers.IO) {
        mStore.removeRecords(keyWord)
    }

    suspend fun getCards(): List<Card> = withContext(Dispatchers.IO) {
        val cards = mStore.getCards()
        cards.sortWith(cardTimeComparator)
        cards
    }

    suspend fun setCard(card: Card): Boolean = withContext(Dispatchers.IO) {
        mStore.upsertCard(card)
    }

    suspend fun removeCards(keyWord: String): Boolean = withContext(Dispatchers.IO) {
        mStore.removeCards(keyWord)
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
