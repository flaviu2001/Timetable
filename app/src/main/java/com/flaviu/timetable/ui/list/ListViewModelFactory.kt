package com.flaviu.timetable.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao
import java.lang.IllegalArgumentException

class ListViewModelFactory(
    private val label: String,
    private val dataSource: CardDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(label, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
