package org.zeroxlab.momodict.db.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull

@Entity(tableName = "dictionaries")
class RoomBook {
    @PrimaryKey
    @NonNull
    var bookName: String? = null

    var version: String? = null
    var wordCount: Int = 0
    var syncWordCount: Int = 0
    var author: String? = null
    var email: String? = null
    var website: String? = null
    var description: String? = null
    var date: String? = null
}
