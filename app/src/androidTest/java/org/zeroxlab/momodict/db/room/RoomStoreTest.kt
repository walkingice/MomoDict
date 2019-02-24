package org.zeroxlab.momodict.db.room

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Card
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import java.util.Date

@RunWith(AndroidJUnit4::class)
class RoomStoreTest {

    private val books = ArrayList<Book>()
    private val alphabetEntries = ArrayList<Entry>()
    private val numEntries = ArrayList<Entry>()
    private val cards = ArrayList<Card>()

    private val records = ArrayList<Record>()
    lateinit var db: RoomStore

    @Before
    fun setUp() {
        val ctx = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(ctx, RoomStore::class.java).build()

        Book().apply { bookName = "alphabet" }
                .run { books.add(this) }

        Book().apply { bookName = "numbers" }
                .run { books.add(this) }

        Entry().apply {
            this.source = "alphabet"
            this.wordStr = "a"
            this.data = "char a"
        }.run { alphabetEntries.add(this) }

        Entry().apply {
            this.source = "alphabet"
            this.wordStr = "b"
            this.data = "char b"
        }.run { alphabetEntries.add(this) }

        Entry().apply {
            this.source = "alphabet"
            this.wordStr = "c"
            this.data = "char c"
        }.run { alphabetEntries.add(this) }

        Entry().apply {
            this.source = "alphabet"
            this.wordStr = "apple"
            this.data = "char apple"
        }.run { alphabetEntries.add(this) }

        Entry().apply {
            this.source = "numbers"
            this.wordStr = "one"
            this.data = "number one"
        }.run { numEntries.add(this) }

        Entry().apply {
            this.source = "numbers"
            this.wordStr = "two"
            this.data = "number two"
        }.run { numEntries.add(this) }

        Entry().apply {
            this.source = "numbers"
            this.wordStr = "three"
            this.data = "number three"
        }.run { numEntries.add(this) }

        Record().apply { wordStr = "ramen" }
                .apply { count = 100 }
                .apply { time = Date() }
                .run { records.add(this) }

        Record().apply { wordStr = "sushi" }
                .apply { count = 30 }
                .apply { time = Date() }
                .run { records.add(this) }

        Record().apply { wordStr = "katsudonn" }
                .apply { count = 20 }
                .apply { time = Date() }
                .run { records.add(this) }

        Card("ramen")
                .apply { note = "My favorite" }
                .apply { time = Date() }
                .run { cards.add(this) }

        Card("sushi")
                .apply { note = "nice food" }
                .apply { time = Date() }
                .run { cards.add(this) }

        Card("katsudonn")
                .apply { note = "eat it everyday" }
                .apply { time = Date() }
                .run { cards.add(this) }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testDbExist() {
        assertNotNull(db)
    }

    @Test
    fun testBook() {
        db.addBook(books[0])
        db.addBook(books[1])

        val list = db.books
        assertNotNull(list)
        assertEquals(list.size, 2)
    }

    @Test
    fun testGetEntries() {
        db.addEntries(alphabetEntries)
        assertEquals(1, db.getEntries("a").size)
    }

    @Test
    fun testQueryEntries() {
        db.addEntries(alphabetEntries)
        var list = db.queryEntries("A") // should be case insensitive
        assertEquals(2, list.size)
        assertEquals("a", list[0].wordStr)
        assertEquals("apple", list[1].wordStr)
    }

    @Test
    fun testQueryEntries2() {
        db.addEntries(alphabetEntries)
        db.addEntries(numEntries)
        var list1 = db.queryEntries("e", "alphabet") // should be case insensitive
        assertEquals(1, list1.size)
        assertEquals("apple", list1[0].wordStr)

        var list2 = db.queryEntries("e", "numbers") // should be case insensitive
        assertEquals(2, list2.size)
        assertEquals("one", list2[0].wordStr)
        assertEquals("three", list2[1].wordStr)
    }

    @Test
    fun testGetRecords() {
        assertEquals(0, db.records.size)
        db.upsertRecord(records[0])
        assertEquals(1, db.records.size)
        assertEquals("ramen", db.records[0].wordStr)

        db.upsertRecord(records[1])
        assertEquals(2, db.records.size)

        db.upsertRecord(records[2])
        assertEquals(3, db.records.size)

        // upsert duplicate, should not add extra record
        db.upsertRecord(records[2])
        assertEquals(3, db.records.size)
    }

    @Test
    fun testUpsertRecord() {
        db.upsertRecord(records[0])
        assertEquals(100, db.records[0].count)

        records[0].count = 101
        db.upsertRecord(records[0])
        assertEquals(101, db.records[0].count)
    }

    @Test
    fun testRemoveRecord() {
        db.upsertRecord(records[0])
        db.upsertRecord(records[1])
        assertEquals(2, db.records.size)

        db.removeRecords(records[0].wordStr)
        assertEquals(1, db.records.size)

        db.removeRecords("not-exist")
        assertEquals(1, db.records.size)
    }

    @Test
    fun testGetCards() {
        assertEquals(0, db.cards.size)

        db.upsertCard(cards[0])
        assertEquals(1, db.cards.size)
        assertEquals(cards[0].wordStr, db.cards[0].wordStr)

        db.upsertCard(cards[1])
        assertEquals(2, db.cards.size)
        assertEquals(cards[1].wordStr, db.cards[1].wordStr)

        db.upsertCard(cards[2])
        assertEquals(3, db.cards.size)
        assertEquals(cards[2].wordStr, db.cards[2].wordStr)
    }

    @Test
    fun testUpsertCard() {
        db.upsertCard(cards[0])
        assertEquals(1, db.cards.size)
        assertEquals(cards[0].wordStr, db.cards[0].wordStr)
        assertEquals(cards[0].note, db.cards[0].note)

        val updatedNote = "I DO LOVE IT".also { note -> cards[0].note = note }

        db.upsertCard(cards[0])
        assertEquals(1, db.cards.size)
        assertEquals(cards[0].wordStr, db.cards[0].wordStr)
        assertEquals(updatedNote, db.cards[0].note)
    }

    @Test
    fun testRemoveCard() {
        db.upsertCard(cards[0])
        db.upsertCard(cards[1])
        assertEquals(2, db.cards.size)

        db.removeCards(cards[0].wordStr)
        assertEquals(1, db.cards.size)

        db.removeCards("not-exist")
        assertEquals(1, db.cards.size)
    }

    @Test
    fun testRemoveBook() {
        db.addBook(books[0])
        db.addBook(books[1])
        db.addEntries(alphabetEntries)
        db.addEntries(numEntries)

        assertEquals(2, db.books.size)
        assertEquals(2, db.queryEntries("a").size) // a and apple
        assertEquals(3, db.queryEntries("e").size) // apple, one and three

        // remove numbers
        db.removeBook(books[1].bookName)
        assertEquals(1, db.books.size)
        assertEquals(2, db.queryEntries("a").size) // a and apple
        assertEquals(1, db.queryEntries("e").size) // apple

    }
}