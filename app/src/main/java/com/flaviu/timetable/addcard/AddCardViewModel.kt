package com.flaviu.timetable.addcard

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*

class AddCardViewModel(
    private val database: CardDatabaseDao
) : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun addCard(
        start: String,
        finish: String,
        weekday: String,
        place: String,
        name: String,
        info: String,
        label: String,
        notes: String
    ) {
        listOf(start, finish, weekday, place, name, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val card = Card(
            timeBegin= start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday),
            place = place,
            name = name,
            info = info,
            label = label,
            notes = notes
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.insert(card)
            }
        }
    }
}