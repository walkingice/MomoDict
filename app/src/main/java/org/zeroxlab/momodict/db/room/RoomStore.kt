package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.model.Store

@Database(entities = arrayOf(RoomBook::class, RoomEntry::class), version = 1)
abstract class RoomStore : Store, RoomDatabase() {

    private val bookDao = getBookDao()
    private val entryDao = getEntryDao()

    override fun addBook(book: Book): Boolean {
        if (book.bookName == null) {
            return false
        }

        val roomBook = RoomBook()
        roomBook.version = book.version
        roomBook.bookName = book.bookName
        roomBook.wordCount = book.wordCount
        roomBook.syncWordCount = book.syncWordCount
        roomBook.author = book.author
        roomBook.email = book.email
        roomBook.website = book.webSite
        roomBook.description = book.description
        roomBook.date = book.date
        bookDao.addBook(roomBook)
        return true
    }

    override fun getBook(name: String): Book {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBooks(): MutableList<Book> {
        return bookDao.getAll().toMutableList()
    }

    override fun removeBook(bookName: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addEntries(entries: MutableList<Entry>): Boolean {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeRecords(keyWord: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecords(): MutableList<Record> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun upsertCard(card: Card): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeCards(keyWord: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCards(): MutableList<Card> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    abstract fun getBookDao(): RoomBookDao
    abstract fun getEntryDao(): RoomEntryDao
}
