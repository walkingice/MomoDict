package org.zeroxlab.momodict.db.room

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    fun timestamp2Date(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun date2Timestamp(date: Date): Long = date.time
}
