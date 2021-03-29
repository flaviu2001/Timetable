package com.flaviu.timetable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flaviu.timetable.database.CardDatabase
import kotlinx.coroutines.*
import java.util.*

class DeletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            null -> {
                val cardId = intent.getLongExtra("cardId", 0)
                val job = Job()
                val scope = CoroutineScope(Dispatchers.Main + job)
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val dao = CardDatabase.getInstance(context).cardDatabaseDao
                        dao.deleteCard(cardId)
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                val job = Job()
                val scope = CoroutineScope(Dispatchers.Main + job)
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val dao = CardDatabase.getInstance(context).cardDatabaseDao
                        val cards = dao.getAllCardsNow()
                        val currentTime = Calendar.getInstance()
                        for (card in cards)
                            if (card.expirationDate != null && card.expirationDate!! > currentTime)
                                scheduleDeletion(
                                    context,
                                    card.cardId,
                                    card.expirationId!!,
                                    card.expirationDate
                                )
                            else if (card.expirationDate != null && card.expirationDate!! <= currentTime) {
                                dao.deleteCard(card.cardId)
                            }
                    }
                }
            }
        }

    }

    companion object {
        private const val CHANNEL_ID = "com.flaviu.timetable.channelId"
    }
}
