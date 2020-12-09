package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_label_table")
data class Label(
    @PrimaryKey(autoGenerate = true)
    var labelId: Int,
    var name: String
)