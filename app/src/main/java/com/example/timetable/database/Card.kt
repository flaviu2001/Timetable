package com.example.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "timetable_card_table")
data class Card (
    @PrimaryKey(autoGenerate = true)
    var cardId: Long = 0L,
    var timeBegin: Int = -1,
    var timeEnd: Int = -1,
    var weekday: Int = -1,
    var place: String,
    var name: String,
    var info: String
)
