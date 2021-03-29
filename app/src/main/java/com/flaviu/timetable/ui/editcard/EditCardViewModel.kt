package com.flaviu.timetable.ui.editcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.scheduleDeletion
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*
import java.util.*

class EditCardViewModel(
    cardKey: Long,
    private var database: CardDatabaseDao,
    private val viewModelApplication: Application
) : AndroidViewModel(viewModelApplication) {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val allLabels = database.getAllLabels()
    val card = database.getCard(cardKey)
    val labelsOfCard = database.getLabelsOfCard(cardKey)
    val mediator = MediatorLiveData<BooleanArray>()

    init {
        mediator.addSource(allLabels) {
            mediator.value = BooleanArray(allLabels.value?.size ?: 0)
            for (i in mediator.value!!.indices)
                mediator.value!![i] = labelsOfCard.value?.contains(allLabels.value?.get(i)) == true
        }
        mediator.addSource(labelsOfCard) {
            mediator.value = BooleanArray(allLabels.value?.size ?: 0)
            for (i in mediator.value!!.indices)
                mediator.value!![i] = labelsOfCard.value?.contains(allLabels.value?.get(i)) == true
        }
    }

    fun onDeleteButtonPressed() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                card.value?.let { database.deleteCard(it.cardId) }
            }
        }
    }

    fun insertLabel(name: String) {
        uiScope.launch {
            database.insertLabel(Label(name = name))
        }
    }

    fun editCard(
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
        reminderId: Int?,
        expirationDate: Calendar?,
        expirationId: Int?
    ) {
        listOf(start, finish, weekday).forEach{
            if (it.isEmpty())
                throw Exception("Start, finish and weekday fields must be completed.")
        }
        if (labelList.isEmpty())
            throw Exception("You must choose at least one label")
        val newCard = card.value ?: throw Exception("Invalid card")
        newCard.place = place
        newCard.info = info
        newCard.name = name
        newCard.timeBegin = start
        newCard.timeEnd = finish
        newCard.weekday = weekdayToInt(weekday, viewModelApplication.resources)
        newCard.color = color
        newCard.textColor = textColor
        newCard.reminderDate = reminderDate
        newCard.reminderId = reminderId
        newCard.expirationDate = expirationDate
        newCard.expirationId = expirationId
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.updateCard(newCard)
                database.deleteAllLabelsFromCard(newCard.cardId)
                for (label in labelList)
                    database.connectLabelToCard(newCard.cardId, label.labelId)
                if (expirationDate != null)
                    scheduleDeletion(viewModelApplication.baseContext, newCard.cardId, expirationId!!, expirationDate)
            }
        }
    }

    fun cloneCard(
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
        reminderId: Int?,
        expirationDate: Calendar?,
        expirationId: Int?
    ) {
        listOf(start, finish, weekday).forEach{
            if (it.isEmpty())
                throw Exception("Start, finish and weekday fields must be completed.")
        }
        if (labelList.isEmpty())
            throw Exception("You must choose at least one label")
        val newCard = Card(timeBegin = start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday, viewModelApplication.resources),
            place = place,
            name = name,
            info = info,
            color = color,
            textColor = textColor,
            reminderDate = reminderDate,
            reminderId = reminderId,
            expirationDate = expirationDate,
            expirationId = expirationId
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val cardId = database.insertCard(newCard)
                for (label in labelList)
                    database.connectLabelToCard(cardId, label.labelId)
                if (expirationDate != null)
                    scheduleDeletion(viewModelApplication.baseContext, cardId, expirationId!!, expirationDate)
            }
        }
    }
}