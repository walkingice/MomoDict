package org.zeroxlab.momodict.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: null safety
@Entity(tableName = "entries")
class Entry {

    @PrimaryKey(autoGenerate = true)
    var entryId: Int = 0

    // Name of the sourceBook of this entry, usually is a dictionary
    var source: String? = null

    var wordStr: String? = null

    var data: String? = null

    override fun toString(): String = "source: $source, wordStr: $wordStr"
}
