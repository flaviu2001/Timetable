package com.flaviu.timetable.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "timetable_card_label_table", primaryKeys = ["cardId", "labelId"])
data class CardLabel(
    @ForeignKey(onDelete = ForeignKey.CASCADE, entity = Card::class, parentColumns = ["cardId"], childColumns = ["cardId"])
    var cardId: Int,
    @ForeignKey(onDelete = ForeignKey.CASCADE, entity = Label::class, parentColumns = ["labelId"], childColumns = ["labelId"])
    var labelId: Int
)