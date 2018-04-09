package org.zeroxlab.momodict.reader

import org.zeroxlab.momodict.archive.Info
import rx.functions.Func1
import rx.functions.Func2
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern

/**
 * A reader to parse .ifo file
 */
class IfoReader {

    private val mInfo = Info()
    private val mMap: Map<String, Func2<Info, String, Info>> = makeMatchers()

    fun parse(inputStream: InputStream): Info {
        inputStream.bufferedReader().useLines { lines -> lines.forEach { parseLine(it) } }
        return mInfo
    }

    private fun parseLine(line: String) {
        val pattern = Pattern.compile("(.+=).*")
        val m = pattern.matcher(line)
        if (m.find()) {
            val prefix = m.group(1)
            if (mMap.containsKey(prefix)) {
                mMap[prefix]!!.call(mInfo, line)
            }
        }
    }

    private fun makeMatchers(): Map<String, Func2<Info, String, Info>> {
        val map = HashMap<String, Func2<Info, String, Info>>()
        map["bookname="] = bookNameMatcher()
        map["version="] = versionMatcher()
        map["author="] = authorMatcher()
        map["website="] = webSiteMatcher()
        map["email="] = emailMatcher()
        map["description="] = descriptionMatcher()
        map["sametypesequence="] = sameTypeSequenceMatcher()
        map["wordcount="] = wordCountMatcher()
        map["syncwordcount="] = syncWordCountMatcher()
        map["idxfilesize="] = idxFileSizeMatcher()
        map["idxoffsets="] = idxOffsetBitsMatcher()
        return map
    }

    private fun createMatcher(key: String): Func1<String, String> {
        return object : Func1<String, String> {
            // ie. build "bookName=XXXX" if key is "bookName"
            internal val pattern = Pattern.compile("$key\\s?=\\s?(.*)")

            override fun call(str: String): String? {
                val tailingSpaceRemoved = str.replace("\\s$".toRegex(), "")
                val matcher = pattern.matcher(tailingSpaceRemoved)
                return if (matcher.find()) matcher.group(1) else null
            }
        }
    }

    private fun bookNameMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("bookname")
        return Func2 { info, str ->
            info.bookName = matchFunc.call(str)
            info
        }
    }

    private fun versionMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("version")
        return Func2 { info, str ->
            info.version = matchFunc.call(str)
            info
        }
    }

    private fun authorMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("author")
        return Func2 { info, str ->
            info.author = matchFunc.call(str)
            info
        }
    }

    private fun webSiteMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("website")
        return Func2 { info, str ->
            info.webSite = matchFunc.call(str)
            info
        }
    }

    private fun emailMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("email")
        return Func2 { info, str ->
            info.email = matchFunc.call(str)
            info
        }
    }


    private fun descriptionMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("description")
        return Func2 { info, str ->
            info.description = matchFunc.call(str)
            info
        }
    }

    private fun sameTypeSequenceMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("sametypesequence")
        return Func2 { info, str ->
            info.sameTypeSequence = matchFunc.call(str)
            info
        }
    }

    private fun wordCountMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("wordcount")
        return Func2 { info, str ->
            val cnt = matchFunc.call(str)
            if (cnt != null) {
                info.wordCount = Integer.parseInt(cnt)
            }
            info
        }
    }

    private fun syncWordCountMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("syncwordcount")
        return Func2 { info, str ->
            val cnt = matchFunc.call(str)
            if (cnt != null) {
                info.syncWordCount = Integer.parseInt(cnt)
            }
            info
        }
    }

    private fun idxFileSizeMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("idxfilesize")
        return Func2 { info, str ->
            val cnt = matchFunc.call(str)
            if (cnt != null) {
                info.idxFileSize = Integer.parseInt(cnt)
            }
            info
        }
    }

    private fun idxOffsetBitsMatcher(): Func2<Info, String, Info> {
        val matchFunc = createMatcher("idxoffsetbits")
        return Func2 { info, str ->
            val cnt = matchFunc.call(str)
            if (cnt != null) {
                info.idxOffsetBits = Integer.parseInt(cnt)
            }
            info
        }
    }

    companion object {

        fun isSanity(info: Info): Boolean {
            // Todo: really Info file sanity check
            return true
        }
    }
}
