package com.flaviu.timetable

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import com.flaviu.timetable.database.CardDatabase
import kotlinx.coroutines.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            null -> {
                val id = intent.getIntExtra("id", 0)
                val job = Job()
                val scope = CoroutineScope(Dispatchers.Main + job)
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val dao = CardDatabase.getInstance(context).cardDatabaseDao
                        val subtask = dao.getSubtaskByReminder(id)
                        val card = dao.getCardByReminder(id)

                        @Suppress("DEPRECATION")
                        val builder =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                                context,
                                CHANNEL_ID
                            )
                            else Notification.Builder(context)
                        val bundle = Bundle()
                        var destination = 0
                        var title = ""
                        var description = ""
                        if (card != null) {
                            title = "Card: ${card.name}"
                            description = "Your activity starts at ${card.timeBegin} on ${
                                context.resources.getStringArray(R.array.weekdays)[card.weekday]
                            }"
                            bundle.putLong("cardKey", card.cardId)
                            destination = R.id.editCardFragment
                            card.reminderDate = null
                            dao.updateCard(card)
                        } else if (subtask != null) {
                            title = "Subtask: ${subtask.description}"
                            description =
                                "Card: ${dao.getCardOfSubtaskNow(subtask.subtaskId)!!.name}"
                            description += if (subtask.reminderDate != null)
                                "\nYour deadline is ${prettyTimeString(subtask.reminderDate)}"
                            else
                                "\nYou have set no deadline"
                            bundle.putLong("cardId", subtask.cardId)
                            bundle.putLong("subtaskId", subtask.subtaskId)
                            destination = R.id.editSubtaskFragment
                            subtask.reminderDate = null
                            dao.updateSubtask(subtask)
                        }
                        val pendingIntent = NavDeepLinkBuilder(context)
                            .setComponentName(MainActivity::class.java)
                            .setGraph(R.navigation.main_navigation)
                            .setDestination(destination)
                            .setArguments(bundle)
                            .createPendingIntent()
                        builder.setContentIntent(pendingIntent)
                        val notificationBuilder = builder.setContentTitle(title)
                            .setStyle(Notification.BigTextStyle().bigText(description))
                            .setSmallIcon(R.drawable.baseline_today_24)
                            .setAutoCancel(true)
                        notificationBuilder.setContentText(description)
                        val notification: Notification = notificationBuilder.build()
                        val notificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel = NotificationChannel(
                                CHANNEL_ID,
                                "Subtask Notification",
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            notificationManager.createNotificationChannel(channel)
                        }
                        notificationManager.notify(id, notification)
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
                        val subtasks = dao.getAllSubtasksNow()
                        for (card in cards)
                            if (card.reminderDate != null)
                                scheduleNotification(
                                    context,
                                    card.reminderId!!,
                                    card.reminderDate
                                )
                        for (subtask in subtasks)
                            if (subtask.reminderDate != null)
                                scheduleNotification(
                                    context,
                                    subtask.reminderId!!,
                                    subtask.reminderDate
                                )
                    }
                }
            }
        }

    }

    companion object {
        private const val CHANNEL_ID = "com.flaviu.timetable.channelId"
    }
}