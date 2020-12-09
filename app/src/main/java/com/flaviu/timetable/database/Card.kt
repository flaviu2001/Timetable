package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "timetable_card_table")
data class Card (
    @PrimaryKey(autoGenerate = true)
    var cardId: Long = 0L,
    var timeBegin: String,
    var timeEnd: String,
    var weekday: Int = -1,
    var place: String,
    var name: String,
    var info: String,
    var label: String,
    var color: Int,
    var textColor: Int
)
