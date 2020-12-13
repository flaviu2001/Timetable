package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "timetable_card_label_table", primaryKeys = ["cardId", "labelId"],
    foreignKeys = [ForeignKey(onDelete = ForeignKey.CASCADE, entity = Card::class, parentColumns = ["cardId"], childColumns = ["cardId"]),
        ForeignKey(onDelete = ForeignKey.CASCADE, entity = Label::class, parentColumns = ["labelId"], childColumns = ["labelId"])])
data class CardLabel(
    var cardId: Long,
    var labelId: Long
)