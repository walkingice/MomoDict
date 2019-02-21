package org.zeroxlab.momodict.db.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cards")
class RoomCard {
    @PrimaryKey
    var wordStr: String = ""

    var note: String? = null

    var time: Date = Date()
}