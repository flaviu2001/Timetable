package com.flaviu.timetable.editcard

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class EditCardViewModel(
    var dataSource: CardDatabaseDao
) : ViewModel() {

}