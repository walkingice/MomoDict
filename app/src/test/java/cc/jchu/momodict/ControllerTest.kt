package cc.jchu.momodict

import cc.jchu.momodict.model.Book
import cc.jchu.momodict.model.Card
import cc.jchu.momodict.model.Entry
import cc.jchu.momodict.model.Record
import cc.jchu.momodict.model.Store
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ControllerTest {
    private val booksList = mutableListOf<Book>()
    private val entriesList = mutableListOf<Entry>()
    private lateinit var mCtrl: Controller

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val ctx = RuntimeEnvironment.application
        mCtrl = Controller(ctx, FakeStore(booksList, entriesList))
    }

    @Test
    @Throws(Exception::class)
    fun testCreation() {
        assertNotNull(mCtrl)
    }

    @Test
    fun testSimpleQuery() =
        runBlocking {
            val entries = mCtrl.queryEntries("one")
            assertEquals(1, entries.size)
            assertEquals("one", entries[0].wordStr)
        }

    @Test
    fun testFuzzyQuery() =
        runBlocking {
            val entries = mCtrl.queryEntries("aaa")
            assertEquals(6, entries.size)
            // "aaa" should be the first one
            assertEquals("aaa", entries[0].wordStr)
        }

    @Test
    fun testFuzzyQuery2() =
        runBlocking {
            val entries = mCtrl.queryEntries("test")
            assertEquals(4, entries.size)
            // "test" should be the first one
            assertEquals("test", entries[0].wordStr)
        }

    private class FakeStore(
        var booksList: MutableList<Book>,
        var entriesList: MutableList<Entry>,
    ) : Store {
        init {
            initBooks(booksList)
            initEntries(entriesList, booksList)
        }

        private fun initBooks(list: MutableList<Book>) {
            Book("alphabet")
                .apply { wordCount = 6 }
                .let { list.add(it) }
            Book("numbers")
                .apply { wordCount = 5 }
                .let { list.add(it) }
        }

        override fun addBook(book: Book): Boolean {
            return booksList.add(book)
        }

        override fun getBook(name: String): Book {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun getBooks(): List<Book> {
            return booksList
        }

        override fun removeBook(bookName: String): Boolean {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun addEntries(entries: List<Entry>): Boolean {
            return entriesList.addAll(entries)
        }

        override fun queryEntries(keyWord: String): MutableList<Entry> {
            val values = mutableListOf<Entry>()
            entriesList
                .filter { it.wordStr.contains(keyWord, true) }
                .let { values.addAll(it) }
            return values
        }

        override fun queryEntries(
            keyWord: String,
            bookName: String,
        ): MutableList<Entry> {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun getEntries(keyWord: String): MutableList<Entry> {
            val values = mutableListOf<Entry>()
            entriesList
                .filter { it.wordStr.equals(keyWord, true) }
                .let { values.addAll(it) }
            return values
        }

        override fun getRecords(): MutableList<Record> {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun upsertRecord(record: Record): Boolean {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun removeRecords(keyWord: String): Boolean {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun getCards(): MutableList<Card> {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun upsertCard(card: Card): Boolean {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        override fun removeCards(keyWord: String): Boolean {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        private fun initEntries(
            list: MutableList<Entry>,
            books: MutableList<Book>,
        ) {
            // alphabet
            Entry("foo prefix_aaa", "content", books[0].bookName).let { list.add(it) }
            Entry("prefix_aaa", "content", books[0].bookName).let { list.add(it) }
            Entry("aaa", "content", books[0].bookName).let { list.add(it) }
            Entry("prefix_2aaa", "content", books[0].bookName).let { list.add(it) }
            Entry("aaa_postfix", "content", books[0].bookName).let { list.add(it) }
            Entry("aaa_postfix bar", "content", books[0].bookName).let { list.add(it) }
            Entry("bbb", "content", books[0].bookName).let { list.add(it) }
            Entry("ccc", "content", books[0].bookName).let { list.add(it) }
            Entry("Comprehensive Test Ban", "content", books[0].bookName).let { list.add(it) }
            Entry("English Test", "content", books[0].bookName).let { list.add(it) }
            Entry("test", "content", books[0].bookName).let { list.add(it) }
            Entry("test (testing)", "content", books[0].bookName).let { list.add(it) }

            // numbers
            Entry("one", "num 1", books[1].bookName).let { list.add(it) }
            Entry("two", "num 2", books[1].bookName).let { list.add(it) }
            Entry("three", "num 3", books[1].bookName).let { list.add(it) }
            Entry("four", "num 4", books[1].bookName).let { list.add(it) }
            Entry("five", "num 5", books[1].bookName).let { list.add(it) }
        }
    }
}
