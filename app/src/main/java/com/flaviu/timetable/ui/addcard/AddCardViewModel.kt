package com.flaviu.timetable.ui.addcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flaviu.timetable.NotificationIdManipulator
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabase
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.scheduleDeletion
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*
import java.util.*

class AddCardViewModel(
    private val database: CardDatabaseDao,
    private val viewModelApplication: Application
) : AndroidViewModel(viewModelApplication) {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val allLabels = CardDatabase.getInstance(viewModelApplication.applicationContext).cardDatabaseDao.getAllLabels()

    fun insertLabel(name: String) {
        uiScope.launch {
            database.insertLabel(Label(name = name))
        }
    }

    fun addCard(
        start: String,
        finish: String,
        weekday: String,
        place: String,
        name: String,
        info: String,
        color: Int,
        textColor: Int,
        labelList: List<Label>,
        reminderDate: Calendar?,
        expirationDate: Calendar?,
        ) {
        listOf(start, finish, weekday).forEach{
            if (it.isEmpty())
                throw Exception("Start, finish and weekday fields must be completed.")
        }
        val card = Card(
            timeBegin= start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday, viewModelApplication.resources),
            place = place,
            name = name,
            info = info,
            color = color,
            textColor = textColor,
            reminderDate = reminderDate,
            reminderId = null,
            expirationDate = expirationDate,
            expirationId = null
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                card.reminderId = NotificationIdManipulator.generateId(viewModelApplication.applicationContext)
                card.expirationId = NotificationIdManipulator.generateId(viewModelApplication.applicationContext)
                val cardId = database.insertCard(card)
                for (label in labelList)
                    database.connectLabelToCard(cardId, label.labelId)
                if (expirationDate != null)
                    scheduleDeletion(viewModelApplication.baseContext, cardId, card.expirationId!!, expirationDate)
            }
        }
    }
}