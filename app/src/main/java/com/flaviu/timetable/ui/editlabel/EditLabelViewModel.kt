package com.flaviu.timetable.ui.editlabel

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class EditLabelViewModel(database: CardDatabaseDao) : ViewModel() {
    val labels = database.getAllLabels()
}