package cc.jchu.momodict.reader

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import cc.jchu.momodict.BuildConfig

@Config(constants = BuildConfig::class)
@RunWith(RobolectricTestRunner::class)
class IdxReaderTest {
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
                .getResourceAsStream("test_dict/test_alphabet/test_alphabet.idx")
                .use {
                    parseIdx(it)
                }
                .also {
                    assertNotNull(it)
                    assertEquals(6, it.size())
                    // 0: a = char a
                    assertEquals("a", it.get(0).wordStr)
                    assertEquals(0, it.get(0).wordDataOffset)
                    assertEquals(6, it.get(0).wordDataSize)
                    // 1: b = char b
                    assertEquals("b", it.get(1).wordStr)
                    assertEquals(6, it.get(1).wordDataOffset)
                    assertEquals(6, it.get(1).wordDataSize)
                    // 2: c = char c
                    assertEquals("c", it.get(2).wordStr)
                    assertEquals(12, it.get(2).wordDataOffset)
                    assertEquals(6, it.get(2).wordDataSize)
                    // 3: d = char d\nline 2
                    assertEquals("d", it.get(3).wordStr)
                    assertEquals(18, it.get(3).wordDataOffset)
                    assertEquals(13, it.get(3).wordDataSize)
                    // 4: e = char e\nline 2
                    assertEquals("e", it.get(4).wordStr)
                    assertEquals(31, it.get(4).wordDataOffset)
                    assertEquals(13, it.get(4).wordDataSize)
                    // 5: f = char f\nline 2
                    assertEquals("f", it.get(5).wordStr)
                    assertEquals(44, it.get(5).wordDataOffset)
                    assertEquals(13, it.get(5).wordDataSize)
                }
    }
}
