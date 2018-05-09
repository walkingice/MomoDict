package org.zeroxlab.momodict.model

// TODO: null safety
class Entry {
    // Name of the sourceBook of this entry, usually is a dictionary
    var source: String? = null

    var wordStr: String? = null

    var data: String? = null

    override fun toString(): String = "source: $source, wordStr: $wordStr"
}
