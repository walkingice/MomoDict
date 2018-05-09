package org.zeroxlab.momodict.model

import java.util.Date

class Card {
    var wordStr: String? = null
    var time: Date? = null
    var note: String? = null

    override fun toString(): String = "wordStr: $wordStr, time: $time, note: $note"
}
