package com.flaviu.timetable.ui.settings

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao
import kotlinx.coroutines.*

class SettingsViewModel (
    private val database: CardDatabaseDao
) : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun clearData() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }
        }
    }
}