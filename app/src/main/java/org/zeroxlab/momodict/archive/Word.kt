package org.zeroxlab.momodict.archive

class Word {
    var entry: IdxEntry? = null
    var data: String? = null

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(entry)
        sb.append("\n")
        sb.append(data)
        return sb.toString()
    }
}
