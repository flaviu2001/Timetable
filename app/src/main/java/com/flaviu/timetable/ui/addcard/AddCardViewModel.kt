package com.flaviu.timetable.ui.addcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*

class AddCardViewModel(
    private val database: CardDatabaseDao,
    private val viewModelApplication: Application
) : AndroidViewModel(viewModelApplication) {
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
        color: Int,
        textColor: Int
    ) {
        listOf(start, finish, weekday, place, name, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val card = Card(
            timeBegin= start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday, viewModelApplication.resources),
            place = place,
            name = name,
            info = info,
            label = label,
            color = color,
            textColor = textColor
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.insert(card)
            }
        }
    }
}