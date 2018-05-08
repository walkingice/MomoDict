package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.TypeConverter
import java.util.Date

class RoomTypeConverter {
    @TypeConverter
    fun timestamp2Date(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun date2Timestamp(date: Date): Long = date.time
}
