package cc.jchu.momodict.reader

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
class InfoReaderTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testParseAlphabet() {
        this.javaClass
                .classLoader
                .getResourceAsStream("test_dict/test_alphabet/test_alphabet.ifo")
                .use { readIfo(it) }
                .also { info ->
                    assertNotNull(info)
                    assertEquals(info.author, "Julian Chu")
                    assertEquals(info.bookName, "Test Dict Alphabet")
                    assertEquals(info.date, "2017.06.04")
                    assertEquals(info.description, "A simple dictionary for testing. Only contains several alphabets.")
                    assertEquals(info.idxFileSize, 60)
                    assertEquals(info.wordCount, 6)
                    assertNull(info.email)
                }
    }
}
