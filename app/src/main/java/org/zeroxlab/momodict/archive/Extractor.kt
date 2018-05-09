package org.zeroxlab.momodict.archive

import java.io.File
import java.io.InputStream

interface Extractor {
    // TODO: proper exception helps user understands what happened
    @Throws(Exception::class)
    fun extract(outputDir: File, inputStream: InputStream): FileSet
}
