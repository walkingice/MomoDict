package org.zeroxlab.momodict.db.realm

import android.content.Context
import android.os.StrictMode
import android.text.TextUtils
import io.realm.Realm
import io.realm.RealmResults
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import org.zeroxlab.momodict.model.Store

class RealmStore(private val mCtx: Context) : Store {

    init {
        // Realm need to load native .so files, turn off StrictMode for it
        val policy = StrictMode.getThreadPolicy()
        StrictMode.allowThreadDiskReads()
        Realm.init(mCtx)
        StrictMode.setThreadPolicy(policy)
    }

    override fun addBook(book: Book): Boolean {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val managedDic = realm.createObject(RealmBook::class.java, book.bookName)
        managedDic.author = book.author
        managedDic.wordCount = book.wordCount
        managedDic.date = book.date
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun getBook(name: String): Book {
        throw RuntimeException("Not implemented yet")
    }

    override fun getBooks(): List<Book> {
        val realm = Realm.getDefaultInstance()
        val dics = realm.where(RealmBook::class.java)
                .findAll()
                .map { managedDic ->
                    Book().also {
                        it.bookName = managedDic.bookName
                        it.author = managedDic.author
                        it.wordCount = managedDic.wordCount
                        it.date = managedDic.date
                    }
                }

        realm.close()
        return dics
    }

    override fun removeBook(bookName: String): Boolean {
        val openedRealm = Realm.getDefaultInstance().also { realm ->
            realm.beginTransaction()
            realm.where(RealmEntry::class.java)
                    .equalTo("sourceBook", bookName)
                    .findAll()
                    .deleteAllFromRealm()
            realm.where(RealmBook::class.java)
                    .equalTo("bookName", bookName)
                    .findAll()
                    .deleteAllFromRealm()
            realm.commitTransaction()
        }
        openedRealm.close()
        return true
    }

    override fun addEntries(entries: List<Entry>): Boolean {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        entries.forEach { entry ->
            realm.createObject(RealmEntry::class.java).also { managedEntry ->
                managedEntry.wordStr = entry.wordStr
                managedEntry.sourceBook = entry.source
                managedEntry.data = entry.data
            }

        }
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun queryEntries(keyWord: String?): List<Entry> {
        val realm = Realm.getDefaultInstance()
        val managedEntries = if (TextUtils.isEmpty(keyWord))
            realm.where(RealmEntry::class.java).findAll()
        else
            realm.where(RealmEntry::class.java).contains("wordStr", keyWord).findAll()

        val mapped = map(managedEntries)
        realm.close()
        return mapped
    }

    override fun getEntries(keyWord: String?): List<Entry> {
        val realm = Realm.getDefaultInstance()
        if (TextUtils.isEmpty(keyWord)) {
            throw RuntimeException("Keyword is empty")
        }
        val managedEntries = realm
                .where(RealmEntry::class.java)
                .equalTo("wordStr", keyWord)
                .findAll()

        val mapped = map(managedEntries)
        realm.close()
        return mapped
    }

    override fun upsertRecord(record: Record): Boolean {
        val realm = Realm.getDefaultInstance()
        val previous = realm.where(RealmRecord::class.java)
                .equalTo("wordStr", record.wordStr)
                .findFirst()
        realm.beginTransaction()
        val managedRecord = previous ?: realm.createObject(RealmRecord::class.java, record.wordStr)
        managedRecord.count = record.count
        managedRecord.time = record.time
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun getRecords(): List<Record> {
        val realm = Realm.getDefaultInstance()
        val records = realm
                .where(RealmRecord::class.java)
                .findAll()
                .map { managedRecord ->
                    Record().also {
                        it.count = managedRecord.count
                        it.wordStr = managedRecord.wordStr
                        it.time = managedRecord.time
                    }
                }
        realm.close()
        return records
    }

    override fun upsertCard(card: Card): Boolean {
        val realm = Realm.getDefaultInstance()
        val previous = realm.where(RealmCard::class.java)
                .equalTo("wordStr", card.wordStr)
                .findFirst()
        realm.beginTransaction()
        val managedCard = previous ?: realm.createObject(RealmCard::class.java, card.wordStr)
        managedCard.time = card.time
        managedCard.note = card.note
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun removeCards(keyWord: String): Boolean {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmCard::class.java)
                .equalTo("wordStr", keyWord)
                .findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun getCards(): List<Card> {
        val realm = Realm.getDefaultInstance()
        val managedCards = realm
                .where(RealmCard::class.java)
                .findAll()

        val cards = ArrayList<Card>()
        for (i in managedCards.indices) {
            val managedCard = managedCards[i]
            val card = Card()
            // FIXME: should remove !!
            card.wordStr = managedCard!!.wordStr
            card.note = managedCard.note
            card.time = managedCard.time
            cards.add(card)
        }
        realm.close()
        return cards
    }

    override fun removeRecords(keyWord: String): Boolean {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmRecord::class.java)
                .equalTo("wordStr", keyWord)
                .findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
        return true
    }

    override fun queryEntries(keyWord: String?, bookName: String): List<Entry> {
        throw RuntimeException("Not implemented yet")
    }

    private fun map(managedEntries: RealmResults<RealmEntry>): List<Entry> {
        val entries = ArrayList<Entry>()
        var i = 0
        while (entries.size < Store.MAX_LENGTH && i < managedEntries.size) {
            val managedEntry = managedEntries[i]
            val entry = Entry()
            // FIXME: should remove !!
            entry.source = managedEntry!!.sourceBook
            entry.wordStr = managedEntry.wordStr
            entry.data = managedEntry.data
            entries.add(entry)
            i++
        }
        return entries
    }
}
