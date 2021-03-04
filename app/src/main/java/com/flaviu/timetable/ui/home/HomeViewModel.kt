package com.flaviu.timetable.ui.home

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class HomeViewModel(
    database: CardDatabaseDao
) : ViewModel() {
    var labels = database.getNonEmptyAndVisibleLabels()
}