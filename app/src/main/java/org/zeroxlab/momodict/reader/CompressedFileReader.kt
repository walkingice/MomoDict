@file:JvmName("CompressedFileReader")

package org.zeroxlab.momodict.reader

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.zeroxlab.momodict.archive.FileSet
import org.zeroxlab.momodict.archive.TarExtractor
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.SecureRandom


// Prefix for directory which contains extracted files
private val DIR_PREFIX = "DICT."

// to generate random string for directory which contains extracted files
private val RANDOM_BITS = 32

/**
 * To extract tar.bz2 file to cache directory.
 *
 * @param outputDir a parent directory. Any cache directory will be under this.
 * @param inputFile the compressed file to be extracted.
 * @return
 */
fun readBzip2File(outputDir: String, inputFile: String): FileSet? {

    // to make a random path such as /tmp/DICT.ru9527
    val random = SecureRandom()
    val integer = BigInteger(RANDOM_BITS, random)
    val randomText = integer.toString(RANDOM_BITS)
    val dirPath = "$outputDir/$DIR_PREFIX$randomText"
    val tmpDir = File(dirPath)
    val made = tmpDir.mkdirs()
    if (!made) {
        throw RuntimeException("Cannot create directory: $tmpDir")
    }

    try {
        // extract bz2 file
        val fis = FileInputStream(File(inputFile))
        return readBzip2File(tmpDir, fis)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}


fun readBzip2File(tmpDir: File, `is`: InputStream): FileSet? {
    try {
        val bis = BufferedInputStream(`is`)
        val b2is = BZip2CompressorInputStream(bis)

        // extract tar file
        val extractor = TarExtractor()
        val archive = extractor.extract(tmpDir, b2is)
        if (!archive.isSane) {
            throw Exception("Necessary files missing: " + archive.toString())
        }
        `is`.close()
        return archive
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

