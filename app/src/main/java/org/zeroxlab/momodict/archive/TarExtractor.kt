package org.zeroxlab.momodict.archive

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.FileUtils
import rx.functions.Action0
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class TarExtractor : Extractor {

    @Throws(Exception::class)
    override fun extract(outputDir: File, inputStream: InputStream): FileSet {
        val archive = FileSet()
        archive.setCleanCallback(Action0 {
            try {
                if (outputDir.isDirectory) {
                    FileUtils.deleteDirectory(outputDir)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })

        val tis = TarArchiveInputStream(inputStream)
        while (tis.nextEntry != null) {
            onEntryFound(outputDir, tis, tis.currentEntry, archive)
        }

        if (!archive.isSane) {
            throw Exception("Book is malformed")
        }
        return archive
    }

    @Throws(Exception::class)
    private fun onEntryFound(parent: File,
                             iStream: InputStream,
                             entry: TarArchiveEntry,
                             archive: FileSet) {

        val fileName = entry.name
        if (entry.isDirectory) {
            val dir = File(parent, fileName)
            if (!dir.mkdirs()) {
                throw Exception("Create dir fail")
            }
        } else {
            // write file
            val out = File(parent, fileName)

            val buf = ByteArray(65535)
            val fos = FileOutputStream(out)
            val bos = BufferedOutputStream(fos)
            var count: Int = iStream.read(buf)
            while (count != -1) {
                bos.write(buf, 0, count)
                count = iStream.read(buf)
            }
            bos.close()

            // cache file path
            if (fileName.endsWith(".idx")) {
                archive[FileSet.Type.IDX] = out.absolutePath
            } else if (fileName.endsWith(".ifo")) {
                archive[FileSet.Type.IFO] = out.absolutePath
            } else if (fileName.endsWith(".dict.dz")) {
                // found a dz file, use gzip to extract again
                val extractFileName = out.absolutePath.replace(".dict.dz", ".dict")
                val fis = FileInputStream(out)
                val gis = GzipCompressorInputStream(fis)
                val fos2 = FileOutputStream(extractFileName)
                val buffer = ByteArray(2048)
                var r = gis.read(buffer)
                while (r != -1) {
                    fos2.write(buffer, 0, r)
                    r = gis.read(buffer)
                }
                fis.close()
                fos2.close()
                archive[FileSet.Type.DICT] = extractFileName
            } else if (fileName.endsWith(".dict")) {
                archive[FileSet.Type.DICT] = out.absolutePath
            }
        }
        println(entry.name)
    }
}
