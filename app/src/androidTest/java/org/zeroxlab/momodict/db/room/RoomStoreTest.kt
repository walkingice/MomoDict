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

@RunWith(AndroidJUnit4::class)
class RoomStoreTest {

    lateinit var db: RoomStore

    @Before
    fun setUp() {
        val ctx = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(ctx, RoomStore::class.java).build()
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
}