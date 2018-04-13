package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

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
