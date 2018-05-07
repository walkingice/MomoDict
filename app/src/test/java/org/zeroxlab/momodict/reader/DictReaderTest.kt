package org.zeroxlab.momodict.reader

import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.zeroxlab.momodict.BuildConfig
import org.zeroxlab.momodict.archive.Idx
import org.zeroxlab.momodict.archive.IdxEntry

@Config(constants = BuildConfig::class)
@RunWith(RobolectricTestRunner::class)
class DictReaderTest {

    val idx = Idx()

    @Before
    fun setUp() {
        // we use fixed dictionary data
        idx.entries.add(IdxEntry("a", 0, 6))
        idx.entries.add(IdxEntry("b", 6, 6))
        idx.entries.add(IdxEntry("c", 12, 6))
        idx.entries.add(IdxEntry("d", 18, 13))
        idx.entries.add(IdxEntry("e", 31, 13))
        idx.entries.add(IdxEntry("f", 44, 13))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testParseAlphabet() {
        this.javaClass
                .classLoader
                .getResourceAsStream("test_dict/test_alphabet/test_alphabet.dict.dz")
                .let { DictReader.wrapInputStream(true, it) }
                .use { stream -> DictReader.parse(idx.entries, stream) }
                .also {
                    assertEquals(6, it.size)
                    assertEquals("a", it[0].entry.wordStr)
                    assertEquals("char a", it[0].data)
                    assertEquals("b", it[1].entry.wordStr)
                    assertEquals("char b", it[1].data)
                    assertEquals("c", it[2].entry.wordStr)
                    assertEquals("char c", it[2].data)

                    assertEquals("d", it[3].entry.wordStr)
                    assertEquals("char d\nline 2", it[3].data)
                    assertEquals("e", it[4].entry.wordStr)
                    assertEquals("char e\nline 2", it[4].data)
                    assertEquals("f", it[5].entry.wordStr)
                    assertEquals("char f\nline 2", it[5].data)
                }
    }
}