package cc.jchu.momodict.archive

/**
 * Data structure to present a .ifo file
 */
data class Info(
    var version: String? = null,
    var bookName: String? = null,
    var wordCount: Int = 0,
    var syncWordCount: Int = 0,
    var idxFileSize: Int = 0,
    var idxOffsetBits: Int = 0,
    var author: String? = null,
    var email: String? = null,
    var webSite: String? = null,
    var description: String? = null,
    var date: String? = null,
    var sameTypeSequence: String? = null,
)
