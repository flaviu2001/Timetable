package com.flaviu.timetable.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = value
            calendar
        }
    }

    @TypeConverter
    fun dateToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }
}
