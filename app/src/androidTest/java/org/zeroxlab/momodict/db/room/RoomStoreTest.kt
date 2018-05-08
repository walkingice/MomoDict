package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.zeroxlab.momodict.model.Book
import org.zeroxlab.momodict.model.Entry
import org.zeroxlab.momodict.model.Record
import java.util.Date

@RunWith(AndroidJUnit4::class)
class RoomStoreTest {

    private val alphabetEntries = ArrayList<Entry>()
    private val numEntries = ArrayList<Entry>()

    private val records = ArrayList<Record>()
    lateinit var db: RoomStore

    @Before
    fun setUp() {
        val ctx = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(ctx, RoomStore::class.java).build()

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
        val bookA = Book()
        bookA.bookName = "Book A"
        bookA.version = "2.4.2"
        val bookB = Book()
        bookB.bookName = "Book B"
        bookB.version = "2.4.2"
        db.addBook(bookA)
        db.addBook(bookB)

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
}