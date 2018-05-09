package org.zeroxlab.momodict.model

import java.util.Date

class Record {
    var wordStr: String? = null
    var count: Int = 0
    var time: Date? = null

    override fun toString(): String = "wordStr: $wordStr, time: $time, count: $count"
}