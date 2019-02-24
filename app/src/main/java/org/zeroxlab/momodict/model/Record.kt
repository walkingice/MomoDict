package org.zeroxlab.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "records")
data class Record(
        @PrimaryKey
        var wordStr: String
) {
    var count: Int = 0
    var time: Date? = null

    override fun toString(): String = "wordStr: $wordStr, time: $time, count: $count"
}
