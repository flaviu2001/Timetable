package com.flaviu.timetable.addcard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao
import java.lang.IllegalArgumentException

class AddCardViewModelFactory (
    private val dataSource: CardDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCardViewModel::class.java)) {
            return AddCardViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}