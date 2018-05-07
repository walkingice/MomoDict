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

@RunWith(AndroidJUnit4::class)
class RoomStoreTest {

    private val alphabetEntries = ArrayList<Entry>()
    private val numEntries = ArrayList<Entry>()
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
}