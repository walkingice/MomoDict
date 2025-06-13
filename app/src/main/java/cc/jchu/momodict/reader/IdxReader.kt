@file:JvmName("IdxReader")

package cc.jchu.momodict.reader

import cc.jchu.momodict.archive.Idx
import cc.jchu.momodict.archive.IdxEntry
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

private const val NULL_TERMINATED = '\u0000'.toByte() // '\0'
private const val NULL_TERMINATED_LENGTH = 1 // length of '\0'
private const val OFFSET_LENGTH = 4
private const val SIZE_LENGTH = 4
private const val BUFFER_SIZE = 8192

fun parseIdx(inputStream: InputStream): Idx {
    val idx = Idx()
    try {
        inputStream.readBytes().let { data -> analysis(data, idx) }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    println("Total:" + idx.size())
    return idx
}

private fun analysis(data: ByteArray, idx: Idx): Int {
    // an index to indicate the last-parsed-byte + 1
    var idxLeft = 0

    val buffer = ByteArrayOutputStream()
    val max = data.size - OFFSET_LENGTH - SIZE_LENGTH
    var idxRight = 0

    // if 'foobar' with offset=0x01020304 size=0a0b0c0d, it will be store in below format
    // foobar\0010203040a0b0c0d
    while (idxRight < max) {
        if (data[idxRight] == NULL_TERMINATED) {
            // read 'foobar'
            buffer.write(data, idxLeft, idxRight - idxLeft)
            val str = buffer.toString()

            idxRight += NULL_TERMINATED_LENGTH // skip '\0'

            // read offset '0x01020304'
            buffer.reset()
            buffer.write(data, idxRight, OFFSET_LENGTH)
            val offsetArray = buffer.toByteArray()
            val offset = ByteBuffer.wrap(offsetArray).int
            idxRight += OFFSET_LENGTH // 64 bits = 8 bytes
            buffer.reset()

            // read size '0x0a0b0c0d'
            buffer.write(data, idxRight, SIZE_LENGTH)
            val sizeArray = buffer.toByteArray()
            val size = ByteBuffer.wrap(sizeArray).int
            buffer.reset()

            idxRight += SIZE_LENGTH
            idxLeft = idxRight

            idx.entries.add(IdxEntry(str, offset, size))
        }
        idxRight++
    }

    return idxLeft
}
