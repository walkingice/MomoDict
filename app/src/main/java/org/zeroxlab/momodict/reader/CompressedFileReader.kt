@file:JvmName("CompressedFileReader")

package org.zeroxlab.momodict.reader

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.zeroxlab.momodict.archive.FileSet
import org.zeroxlab.momodict.archive.TarExtractor
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.SecureRandom


// Prefix for directory which contains extracted files
private val DIR_PREFIX = "DICT."

// to generate random string for directory which contains extracted files
private val RANDOM_BITS = 32

fun makeTempDir(parentDir: File): File {
    val random = SecureRandom()
    val integer = BigInteger(RANDOM_BITS, random)
    val randomText = integer.toString(RANDOM_BITS)
    val dirPath = "$parentDir.path/$DIR_PREFIX$randomText"
    val tmpDir = File(dirPath)
    val made = tmpDir.mkdirs()
    return if (made) tmpDir else throw RuntimeException("Cannot create directory: $tmpDir")
}

/**
 * To extract tar.bz2 file to cache directory.
 *
 * @param outputDir a parent directory. Any cache directory will be under this.
 * @param stream the input stream of the compressed file to be extracted.
 * @return
 */
fun readBzip2File(outputDir: File, stream: InputStream): FileSet? {
    try {
        val bis = BufferedInputStream(stream)
        val b2is = BZip2CompressorInputStream(bis)

        // extract tar file
        val extractor = TarExtractor()
        val archive = extractor.extract(outputDir, b2is)
        if (!archive.isSane) {
            throw Exception("Necessary files missing: " + archive.toString())
        }
        stream.close()
        return archive
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

