package com.flaviu.timetable.home

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class HomeViewModel(
    database: CardDatabaseDao
) : ViewModel() {
    var cards = database.getAllNights()

}