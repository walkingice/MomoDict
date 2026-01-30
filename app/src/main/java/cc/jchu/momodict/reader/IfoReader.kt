@file:JvmName("IfoReader")

package cc.jchu.momodict.reader

import cc.jchu.momodict.archive.Info
import java.io.InputStream
import java.util.regex.Pattern

fun isSanity(info: Info): Boolean {
    // Todo: really Info file sanity check
    return true
}

fun readIfo(inputStream: InputStream): Info {
    val info = Info()
    inputStream.bufferedReader().useLines { lines -> lines.forEach { parseLine(it, info) } }
    return info
}

private fun parseLine(
    line: String,
    info: Info,
) {
    val pattern = Pattern.compile("(.+)=(.*)")
    val m = pattern.matcher(line)
    if (m.find()) {
        val key = m.group(1)
        val value = m.group(2)
        when (key) {
            "author" -> info.author = value
            "bookname" -> info.bookName = value
            "version" -> info.version = value
            "website" -> info.webSite = value
            "email" -> info.email = value
            "description" -> info.description = value
            "sametypesequence" -> info.sameTypeSequence = value
            "date" -> info.date = value
            "wordcount" -> info.wordCount = Integer.parseInt(value)
            "syncwordcount" -> info.syncWordCount = Integer.parseInt(value)
            "idxfilesize" -> info.idxFileSize = Integer.parseInt(value)
            "idxoffsets" -> info.idxOffsetBits = Integer.parseInt(value)
        }
    }
}
