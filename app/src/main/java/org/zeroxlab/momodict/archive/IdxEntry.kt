package org.zeroxlab.momodict.archive

/**
 * A data structure to present .idx file
 */
data class IdxEntry(var wordStr: String,
                    var wordDataOffset: Int,
                    var wordDataSize: Int)
