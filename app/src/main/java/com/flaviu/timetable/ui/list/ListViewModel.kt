package com.flaviu.timetable.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class ListViewModel (
    label: String,
    database: CardDatabaseDao
) : ViewModel() {
    var cards = database.getCardsWithLabel(label)

    var navigateToEditCard = MutableLiveData<Long>()

    fun onCardClicked(cardKey: Long) {
        navigateToEditCard.value = cardKey
    }

    fun onEditCardNavigated() {
        navigateToEditCard.value = null
    }
}