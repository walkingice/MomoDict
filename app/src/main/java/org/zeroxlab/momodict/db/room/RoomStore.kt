package org.zeroxlab.momodict.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.model.Store

@Database(
        entities = arrayOf(
                Book::class,
                RoomEntry::class,
                RoomCard::class,
                RoomRecord::class),
        version = 1)
@TypeConverters(RoomTypeConverter::class)
abstract class RoomStore : Store, RoomDatabase() {

    private val bookDao = getBookDao()
    private val entryDao = getEntryDao()
    private val recordDao = getRecordDao()
    private val cardDao = getCardDao()

    override fun addBook(book: Book): Boolean {
        bookDao.addBook(book)
        return true
    }

    override fun getBook(name: String): Book {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBooks(): MutableList<Book> {
        return bookDao.getAll().toMutableList()
    }

    override fun removeBook(bookName: String): Boolean {
        // TODO: use foreign key might be better
        val removed = bookDao.removeBook(bookName)
        entryDao.removeEntriesByBookName(bookName)
        return removed > 0
    }

    override fun addEntries(entries: List<Entry>): Boolean {
        val list = mutableListOf<RoomEntry>()
        for (entry in entries) {
            val re = RoomEntry()
            re.source = entry.source
            re.data = entry.data
            re.wordStr = entry.wordStr
            list.add(re)
        }
        entryDao.addEntries(list)
        return true
    }

    override fun queryEntries(keyWord: String?): MutableList<Entry> {
        return entryDao.queryEntries(keyWord!!).toMutableList()
    }

    override fun queryEntries(keyWord: String?, bookName: String): MutableList<Entry> {
        return entryDao.queryEntries(keyWord!!, bookName!!).toMutableList()
    }

    override fun getEntries(keyWord: String?): MutableList<Entry> {
        return entryDao.getEntries(keyWord!!).toMutableList()
    }

    override fun upsertRecord(record: Record): Boolean {
        return RoomRecord()
                .also {
                    it.count = record.count
                    it.wordStr = record.wordStr!!
                    it.time = record.time!!
                }
                .apply { recordDao.addRecord(this) }
                .run { recordDao.updateRecord(this) != -1 }
    }

    override fun removeRecords(keyWord: String): Boolean {
        return recordDao.removeRecord(keyWord) > 0
    }

    override fun getRecords(): MutableList<Record> {
        return recordDao.getRecords().toMutableList()
    }

    override fun upsertCard(card: Card): Boolean {
        return RoomCard()
                .also {
                    it.wordStr = card.wordStr!!
                    it.note = card.note!!
                    it.time = card.time!!
                }
                .apply { cardDao.addCard(this) }
                .run { cardDao.updateCard(this) != -1 }
    }

    override fun removeCards(keyWord: String): Boolean {
        return cardDao.removeCard(keyWord) > 0
    }

    override fun getCards(): MutableList<Card> {
        return cardDao.getCards().toMutableList()
    }

    abstract fun getBookDao(): BookDao
    abstract fun getEntryDao(): RoomEntryDao
    abstract fun getRecordDao(): RoomRecordDao
    abstract fun getCardDao(): RoomCardDao

    companion object {
        val DB_NAME = "room_db"
    }
}
