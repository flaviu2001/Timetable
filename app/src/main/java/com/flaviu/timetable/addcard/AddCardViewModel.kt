package com.flaviu.timetable.addcard

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import kotlinx.coroutines.*
import java.lang.Exception

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
        info: String
    ) {
        listOf(start, finish, weekday, place, name, info).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        Log.i("AddCardViewModel", start)
        if (start.toInt() !in 0..23)
            throw Exception("Start hour invalid. Must be between 0 and 23")
        if (finish.toInt() !in 0..23)
            throw Exception("Finish hour invalid. Must be between 0 and 23")
        if (weekday.toInt() !in 0..6) //TODO: Change to dropdown list
            throw Exception("Weekday invalid. Must be between 0 and 6")
        val card = Card(
            timeBegin= start.toInt(),
            timeEnd = finish.toInt(),
            weekday = weekday.toInt(),
            place = place,
            name = name,
            info = info
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.insert(card)
            }
        }
    }
}