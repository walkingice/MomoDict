package cc.jchu.momodict.db.room

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import cc.jchu.momodict.model.Book
import cc.jchu.momodict.model.Card
import cc.jchu.momodict.model.Entry
import cc.jchu.momodict.model.Record
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

        Book("alphabet").run { books.add(this) }

        Book("numbers").run { books.add(this) }

        Entry("a").apply {
            this.source = "alphabet"
            this.data = "char a"
        }.run { alphabetEntries.add(this) }

        Entry("b").apply {
            this.source = "alphabet"
            this.data = "char b"
        }.run { alphabetEntries.add(this) }

        Entry("c").apply {
            this.source = "alphabet"
            this.data = "char c"
        }.run { alphabetEntries.add(this) }

        Entry("apple").apply {
            this.source = "alphabet"
            this.data = "char apple"
        }.run { alphabetEntries.add(this) }

        Entry("one").apply {
            this.source = "numbers"
            this.data = "number one"
        }.run { numEntries.add(this) }

        Entry("two").apply {
            this.source = "numbers"
            this.data = "number two"
        }.run { numEntries.add(this) }

        Entry("three").apply {
            this.source = "numbers"
            this.data = "number three"
        }.run { numEntries.add(this) }

        Record("ramen")
                .apply { count = 100 }
                .apply { time = Date() }
                .run { records.add(this) }

        Record("sushi")
                .apply { count = 30 }
                .apply { time = Date() }
                .run { records.add(this) }

        Record("katsudonn")
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

        val list = db.getBooks()
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
        assertEquals(0, db.getRecords().size)
        db.upsertRecord(records[0])
        assertEquals(1, db.getRecords().size)
        assertEquals("ramen", db.getRecords()[0].wordStr)

        db.upsertRecord(records[1])
        assertEquals(2, db.getRecords().size)

        db.upsertRecord(records[2])
        assertEquals(3, db.getRecords().size)

        // upsert duplicate, should not add extra record
        db.upsertRecord(records[2])
        assertEquals(3, db.getRecords().size)
    }

    @Test
    fun testUpsertRecord() {
        db.upsertRecord(records[0])
        assertEquals(100, db.getRecords()[0].count)

        records[0].count = 101
        db.upsertRecord(records[0])
        assertEquals(101, db.getRecords()[0].count)
    }

    @Test
    fun testRemoveRecord() {
        db.upsertRecord(records[0])
        db.upsertRecord(records[1])
        assertEquals(2, db.getRecords().size)

        db.removeRecords(records[0].wordStr)
        assertEquals(1, db.getRecords().size)

        db.removeRecords("not-exist")
        assertEquals(1, db.getRecords().size)
    }

    @Test
    fun testGetCards() {
        assertEquals(0, db.getCards().size)

        db.upsertCard(cards[0])
        assertEquals(1, db.getCards().size)
        assertEquals(cards[0].wordStr, db.getCards()[0].wordStr)

        db.upsertCard(cards[1])
        assertEquals(2, db.getCards().size)
        assertEquals(cards[1].wordStr, db.getCards()[1].wordStr)

        db.upsertCard(cards[2])
        assertEquals(3, db.getCards().size)
        assertEquals(cards[2].wordStr, db.getCards()[2].wordStr)
    }

    @Test
    fun testUpsertCard() {
        db.upsertCard(cards[0])
        assertEquals(1, db.getCards().size)
        assertEquals(cards[0].wordStr, db.getCards()[0].wordStr)
        assertEquals(cards[0].note, db.getCards()[0].note)

        val updatedNote = "I DO LOVE IT".also { note -> cards[0].note = note }

        db.upsertCard(cards[0])
        assertEquals(1, db.getCards().size)
        assertEquals(cards[0].wordStr, db.getCards()[0].wordStr)
        assertEquals(updatedNote, db.getCards()[0].note)
    }

    @Test
    fun testRemoveCard() {
        db.upsertCard(cards[0])
        db.upsertCard(cards[1])
        assertEquals(2, db.getCards().size)

        db.removeCards(cards[0].wordStr)
        assertEquals(1, db.getCards().size)

        db.removeCards("not-exist")
        assertEquals(1, db.getCards().size)
    }

    @Test
    fun testRemoveBook() {
        db.addBook(books[0])
        db.addBook(books[1])
        db.addEntries(alphabetEntries)
        db.addEntries(numEntries)

        assertEquals(2, db.getBooks().size)
        assertEquals(2, db.queryEntries("a").size) // a and apple
        assertEquals(3, db.queryEntries("e").size) // apple, one and three

        // remove numbers
        db.removeBook(books[1].bookName)
        assertEquals(1, db.getBooks().size)
        assertEquals(2, db.queryEntries("a").size) // a and apple
        assertEquals(1, db.queryEntries("e").size) // apple
    }
}
