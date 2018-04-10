package org.zeroxlab.momodict.reader

import org.zeroxlab.momodict.archive.IdxEntry
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

/**
 * A reader to parse .idx file
 */
class IdxReader {

    private val mEntries = ArrayList<IdxEntry>()

    val entries: List<IdxEntry>
        get() = mEntries

    operator fun get(idx: Int): IdxEntry {
        return mEntries[idx]
    }

    fun size(): Int {
        return mEntries.size
    }

    fun parse(inputStream: InputStream) {
        val buffer = ByteArray(BUFFER_SIZE)
        try {
            val baos = ByteArrayOutputStream()
            var readCnt: Int
//            val data = inputStream.readBytes(BUFFER_SIZE)
//            inputStream.readBytes{ lines -> lines.forEach { parseLine(it, info) } }
            while (true) {
                readCnt = inputStream.read(buffer)
                if (readCnt <= 0) {
                    break
                }
                baos.write(buffer, 0, readCnt)
                val data = baos.toByteArray()
                val fresh = analysis(data)
                baos.reset()
                baos.write(data, fresh, data.size - fresh)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        println("Total:" + mEntries.size)
    }

    private fun analysis(data: ByteArray): Int {
        // an index to indicate the last-parsed-byte + 1
        var idxFresh = 0

        val baos = ByteArrayOutputStream()
        val max = data.size - OFFSET_LENGTH - SIZE_LENGTH
        var i = 0
        while (i < max) {
            if (data[i] == '\u0000'.toByte()) {
                baos.write(data, idxFresh, i - idxFresh)
                val str = baos.toString()

                i += NULL_TERMINATED_LENGTH // skip '\0'
                baos.reset()
                baos.write(data, i, OFFSET_LENGTH)
                val offsetArray = baos.toByteArray()
                val offset = ByteBuffer.wrap(offsetArray).int
                i += OFFSET_LENGTH // 64 bits = 8 bytes
                baos.reset()

                baos.write(data, i, SIZE_LENGTH)
                val sizeArray = baos.toByteArray()
                val size = ByteBuffer.wrap(sizeArray).int
                baos.reset()

                i += SIZE_LENGTH
                idxFresh = i

                mEntries.add(IdxEntry(str, offset, size))
            }
            i++
        }

        return idxFresh
    }

    companion object {
        private const val NULL_TERMINATED_LENGTH = 1 // length of '\0'
        private const val OFFSET_LENGTH = 4
        private const val SIZE_LENGTH = 4
        private const val BUFFER_SIZE = 8192
    }
}
