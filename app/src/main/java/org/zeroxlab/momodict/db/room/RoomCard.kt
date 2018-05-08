package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cards")
class RoomCard {
    @PrimaryKey
    var wordStr: String = ""

    var note: String = ""

    var time: Date = Date()
}