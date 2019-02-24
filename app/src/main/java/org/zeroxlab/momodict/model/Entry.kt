package org.zeroxlab.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: null safety
@Entity(tableName = "entries")
data class Entry(
        var wordStr: String
) {
    @PrimaryKey(autoGenerate = true)
    var entryId: Int = 0

    // Name of the sourceBook of this entry, usually is a dictionary
    var source: String? = null

    var data: String? = null
}
