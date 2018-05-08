package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity(tableName = "records")
class RoomRecord {

    @PrimaryKey
    var wordStr: String = ""

    var count: Int = 0

    var time: Date = Date()
}