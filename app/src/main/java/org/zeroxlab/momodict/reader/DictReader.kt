@file:JvmName("DictReader")

package org.zeroxlab.momodict.reader

import org.zeroxlab.momodict.archive.IdxEntry
import org.zeroxlab.momodict.archive.Word
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.zip.GZIPInputStream

@Throws(IOException::class)
fun wrapInputStream(isDzFile: Boolean, stream: InputStream): InputStream {
    return if (isDzFile) GZIPInputStream(stream) else stream
}

fun parseDict(entries: List<IdxEntry>, stream: InputStream): List<Word> {
    val words = ArrayList<Word>()
    try {
        for (entry in entries) {
            val word = Word()
            val data = ByteArray(entry.wordDataSize)
            stream.read(data)
            word.data = String(data)
            word.entry = entry
            words.add(word)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        stream.close()
    }

    return words
}

fun read(entry: IdxEntry, dict: File) {
    try {
        val rf = RandomAccessFile(dict, "r")
        val data = ByteArray(entry.wordDataSize)
        rf.seek(entry.wordDataOffset.toLong())
        rf.read(data, 0, entry.wordDataSize)
        rf.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
