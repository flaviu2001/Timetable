package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "timetable_subtask_table", foreignKeys = [ForeignKey(onDelete = CASCADE, entity = Card::class, parentColumns = ["cardId"], childColumns = ["cardId"])])
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    var subtaskId: Long = 0L,
    var cardId: Long,
    var description: String,
    var dueDate: Calendar?,
    var reminderDate: Calendar?,
    var reminderId: Int
)
