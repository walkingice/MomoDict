package cc.jchu.momodict.reader

import androidx.room.Room
import android.content.Context
import cc.jchu.momodict.archive.FileSet
import cc.jchu.momodict.db.room.RoomStore
import cc.jchu.momodict.model.Book
import cc.jchu.momodict.model.Entry
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.ArrayList

/**
 * A class to extract compressed file, to read and to save into Database.
 */
class Reader
/**
 * Constructor
 *
 * @param mCacheDir A string as path of cache directory. Extracted files will be placed
 * here.
 * @param mFilePath A string as path of a compressed file which will be parsed.
 */
    (private val mCacheDir: String, private val mFilePath: String) {

    /**
     * To read file and save into database.
     *
     * @param ctx Context instance
     */
    fun parse(ctx: Context) {
        // extract file
        val cachedir = File(mCacheDir)
        val outputDir = makeTempDir(cachedir)
        var archive: FileSet? = null
        try {
            val fis = FileInputStream(File(mFilePath))
            archive = readBzip2File(outputDir, fis)
            // FIXME: should avoid main thread
            val store = Room.databaseBuilder(
                ctx.applicationContext,
                RoomStore::class.java,
                RoomStore.DB_NAME
            )
                .allowMainThreadQueries()
                .build()
            val ifoFile = File(archive!![FileSet.Type.IFO]!!)
            val idxFile = File(archive[FileSet.Type.IDX]!!)

            // To read ifo file
            val `is` = FileInputStream(ifoFile)
            val info = readIfo(`is`)
            `is`.close()
            if (!isSanity(info)) {
                throw RuntimeException("Insanity .ifo file")
            }

            // To read idx file
            if (idxFile == null || !idxFile.exists()) {
                throw RuntimeException("Should give an existing idx file")
            }
            val idxIs = FileInputStream(idxFile)
            val idx = parseIdx(idxIs)
            idxIs.close()

            // To save ifo to database
            // TODO: remove !!
            val dict = Book(info.bookName!!)
            dict.author = info.author
            dict.wordCount = info.wordCount
            dict.date = info.date
            store.addBook(dict)

            // To save each words to database
            if (idx.size() != 0) {
                val dictPath = archive[FileSet.Type.DICT]
                val isDict = dictPath!!.endsWith(".dz")
                val ips = FileInputStream(dictPath)
                val bis = BufferedInputStream(ips)
                val dictIs = wrapInputStream(isDict, bis)
                val words = parseDict(idx.entries, dictIs)
                val entries = ArrayList<Entry>()
                for (word in words) {
                    if (word.entry != null) {
                        val entry = Entry(word.entry!!.wordStr)
                        entry.source = info.bookName
                        entry.data = word.data
                        entries.add(entry)
                    }
                }
                store.addEntries(entries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (archive != null) {
                archive.clean()
            }
        }
    }
}
