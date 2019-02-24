package org.zeroxlab.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cards")
class Card(
        @PrimaryKey
        var wordStr: String
) {
    var time: Date? = null
    var note: String? = null

    override fun toString(): String = "wordStr: $wordStr, time: $time, note: $note"
}
