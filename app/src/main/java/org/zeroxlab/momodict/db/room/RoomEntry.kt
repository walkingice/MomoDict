package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Entity
import android.support.annotation.NonNull

@Entity(tableName = "entries", primaryKeys = arrayOf("source", "wordStr"))
class RoomEntry {
    @NonNull
    var source: String? = null

    @NonNull
    var wordStr: String? = null

    var data: String? = null
}