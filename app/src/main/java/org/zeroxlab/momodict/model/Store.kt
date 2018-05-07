package org.zeroxlab.momodict.model

interface Store {

    val books: List<Book>

    val records: List<Record>

    val cards: List<Card>

    fun addBook(book: Book): Boolean

    fun getBook(name: String): Book

    fun removeBook(bookName: String): Boolean

    fun addEntries(entries: List<Entry>): Boolean

    fun queryEntries(keyWord: String?): List<Entry>

    fun queryEntries(keyWord: String?, bookName: String): List<Entry>

    fun getEntries(keyWord: String?): List<Entry>

    fun upsertRecord(record: Record): Boolean

    fun removeRecords(keyWord: String): Boolean

    fun upsertCard(card: Card): Boolean

    fun removeCards(keyWord: String): Boolean

    companion object {

        val MAX_LENGTH = 300
    }
}
