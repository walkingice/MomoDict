package org.zeroxlab.momodict.db.room

import androidx.room.Entity
import androidx.annotation.NonNull

@Entity(tableName = "entries", primaryKeys = arrayOf("source", "wordStr"))
class RoomEntry {
    @NonNull
    var source: String? = null

    @NonNull
    var wordStr: String? = null

    var data: String? = null
}