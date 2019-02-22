package org.zeroxlab.momodict.db.room

import androidx.room.Entity
import androidx.annotation.NonNull
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
class RoomEntry {

    @PrimaryKey(autoGenerate = true)
    var entryId: Int = 0

    @NonNull
    var source: String? = null

    @NonNull
    var wordStr: String? = null

    var data: String? = null
}