package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_label_table")
data class Label(
    @PrimaryKey(autoGenerate = true)
    var labelId: Long = 0L,
    var visible: Int = 1, //1 means visible
    var name: String
)