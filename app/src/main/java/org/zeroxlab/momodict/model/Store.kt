package org.zeroxlab.momodict.model

interface Store {

    fun addBook(book: Book): Boolean

    fun getBook(name: String): Book

    fun getBooks(): List<Book>

    fun removeBook(bookName: String): Boolean

    fun addEntries(entries: List<Entry>): Boolean

    fun queryEntries(keyWord: String?): MutableList<Entry>

    fun queryEntries(keyWord: String?, bookName: String): List<Entry>

    fun getEntries(keyWord: String?): List<Entry>

    fun getRecords(): MutableList<Record>

    fun upsertRecord(record: Record): Boolean

    fun removeRecords(keyWord: String): Boolean

    fun getCards(): List<Card>

    fun upsertCard(card: Card): Boolean

    fun removeCards(keyWord: String): Boolean

    companion object {
        val MAX_LENGTH = 300
    }
}
