package org.zeroxlab.momodict.db.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "records")
class RoomRecord {

    @PrimaryKey
    var wordStr: String = ""

    var count: Int = 0

    var time: Date = Date()
}