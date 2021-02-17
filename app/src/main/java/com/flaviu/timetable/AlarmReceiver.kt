package com.flaviu.timetable

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.flaviu.timetable.database.CardDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val id = intent.getIntExtra("id", 0)
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Main + job)
        scope.launch {
            val dao = CardDatabase.getInstance(context).cardDatabaseDao
            val subtask = dao.getSubtaskByReminder(id)
            val task = dao.getCardByReminder(id)
            // Bit of a dirty hack, not exactly well thought but I check whether the id is found in either cards or subtasks. This way I don't have to make two alarm receivers
            if (subtask != null || task != null) {
                @Suppress("DEPRECATION")
                val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(context, CHANNEL_ID)
                else Notification.Builder(context)
                val notificationBuilder = builder.setContentTitle(title)
                    .setStyle(Notification.BigTextStyle().bigText(description))
                    .setSmallIcon(R.drawable.baseline_today_24)
                if (description != null)
                    notificationBuilder.setContentText(description)
                val notification: Notification = notificationBuilder.build()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    companion object {
        private const val CHANNEL_ID = "com.flaviu.timetable.channelId"
    }
}