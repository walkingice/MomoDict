package cc.jchu.momodict.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.jchu.momodict.model.Book
import cc.jchu.momodict.model.Card
import cc.jchu.momodict.model.Entry
import cc.jchu.momodict.model.Record
import cc.jchu.momodict.model.Store

@Database(
    entities =
        arrayOf(
            Book::class,
            Entry::class,
            Card::class,
            Record::class,
        ),
    version = 1,
)
@TypeConverters(TypeConverter::class)
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
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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
        entryDao.addEntries(entries)
        return true
    }

    override fun queryEntries(keyWord: String): MutableList<Entry> {
        return entryDao.queryEntries(keyWord).toMutableList()
    }

    override fun queryEntries(
        keyWord: String,
        bookName: String,
    ): MutableList<Entry> {
        return entryDao.queryEntries(keyWord, bookName!!).toMutableList()
    }

    override fun getEntries(keyWord: String): MutableList<Entry> {
        return entryDao.getEntries(keyWord).toMutableList()
    }

    override fun upsertRecord(record: Record): Boolean {
        return record
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
        return card
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

    abstract fun getEntryDao(): EntryDao

    abstract fun getRecordDao(): RecordDao

    abstract fun getCardDao(): CardDao

    companion object {
        val DB_NAME = "room_db"
    }
}
