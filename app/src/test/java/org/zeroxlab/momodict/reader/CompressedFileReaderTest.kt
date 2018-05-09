package org.zeroxlab.momodict.reader

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.zeroxlab.momodict.BuildConfig
import org.zeroxlab.momodict.archive.FileSet
import java.io.File

@Config(constants = BuildConfig::class)
@RunWith(RobolectricTestRunner::class)
class CompressedFileReaderTest {

    private lateinit var tmpFolder: TemporaryFolder
    private lateinit var tmpDir: File

    @Before
    fun setUp() {
        tmpFolder = TemporaryFolder()
        tmpFolder.create()
        tmpDir = tmpFolder.newFolder()
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
    }

    @Test
    fun testMakeTempDir() {
        val tmp = makeTempDir(tmpDir)

        // created tmp directory should be child of tmpDir
        assertTrue(tmp.path.startsWith(tmpDir.path))
    }

    @Test
    fun testResourceExist() {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("test_dict/test_alphabet.tar.bz2")
        assertNotNull(inputStream)
        assertNotNull(tmpFolder)
        assertEquals(629, inputStream.available())
    }

    @Test
    fun testExtractFile() {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("test_dict/test_alphabet.tar.bz2")
        val fileSet = readBzip2File(tmpDir, inputStream)
        assertNotNull(fileSet)
        assertTrue(fileSet!!.isSane)
        assertTrue(fileSet!!.has(FileSet.Type.IFO))
        assertTrue(fileSet!!.has(FileSet.Type.IDX))
        assertTrue(fileSet!!.has(FileSet.Type.DICT))
        assertTrue(fileSet!!.get(FileSet.Type.IFO)!!.endsWith("test_alphabet.ifo"))
        assertTrue(fileSet!!.get(FileSet.Type.IDX)!!.endsWith("test_alphabet.idx"))
        assertTrue(fileSet!!.get(FileSet.Type.DICT)!!.endsWith("test_alphabet.dict"))
    }
}
