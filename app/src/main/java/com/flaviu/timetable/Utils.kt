package com.flaviu.timetable

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.flaviu.timetable.database.*
import com.flaviu.timetable.ui.MainActivity
import kotlinx.coroutines.*
import top.defaults.drawabletoolbox.DrawableBuilder
import java.io.*
import java.util.*

val preset_colors: IntArray = listOf(
    0xFF751600, // My dark red
    0xFF960B0B, // My red
    0xFF880E4F, // My red purple
    0xFF9C27B0, // PURPLE 500
    0xFF4a148c, // My blue purple
    0xFF673AB7, // DEEP PURPLE 500
    0xFF003B8E, // My dark blue
    0xFF3F51B5, // INDIGO 500
    0xFF004D40, // My teal
    0xFF1B5E20, // My dark green
    0xFF966C05, // My yellow
    0xFFA54610, // My orange
    0xFF3E2723, // My ???
    0xFF444444, // My Grey
    0xFF000000, // My black
    0xFFF44336, // RED 500
    0xFFE91E63, // PINK 500
    0xFFFF2C93, // LIGHT PINK 500
    0xFFBA68C8, // My light purple
    0xFF2196F3, // BLUE 500
    0xFF03A9F4, // LIGHT BLUE 500
    0xFF00BCD4, // CYAN 500
    0xFF90CAF9, // My light blue
    0xFF009688, // TEAL 500
    0xFF4CAF50, // GREEN 500
    0xFF8BC34A, // LIGHT GREEN 500
    0xFFCDDC39, // LIME 500
    0xFFFF9800, // ORANGE 500
    0xFFFFC107, // AMBER 500
    0xFFFFEB3B, // YELLOW 500
    0xFFFFFFFF, // My white
).map { it.toInt() }.toIntArray()

val text_preset_colors: IntArray = listOf(
    0xFFFFFFFF, // My white
    0xFF000000, // My black
    0xFF751600, // My dark red
    0xFF960B0B, // My red
    0xFF880E4F, // My red purple
    0xFF9C27B0, // PURPLE 500
    0xFF4a148c, // My blue purple
    0xFF673AB7, // DEEP PURPLE 500
    0xFF003B8E, // My dark blue
    0xFF3F51B5, // INDIGO 500
    0xFF004D40, // My teal
    0xFF1B5E20, // My dark green
    0xFF966C05, // My yellow
    0xFFA54610, // My orange
    0xFF3E2723, // My ???
    0xFF444444, // My Grey
    0xFFF44336, // RED 500
    0xFFE91E63, // PINK 500
    0xFFFF2C93, // LIGHT PINK 500
    0xFFBA68C8, // My light purple
    0xFF2196F3, // BLUE 500
    0xFF03A9F4, // LIGHT BLUE 500
    0xFF00BCD4, // CYAN 500
    0xFF90CAF9, // My light blue
    0xFF009688, // TEAL 500
    0xFF4CAF50, // GREEN 500
    0xFF8BC34A, // LIGHT GREEN 500
    0xFFCDDC39, // LIME 500
    0xFFFF9800, // ORANGE 500
    0xFFFFC107, // AMBER 500
    0xFFFFEB3B, // YELLOW 500
).map { it.toInt() }.toIntArray()

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun timeFormatter(hour: Int, minutes: Int): String {
    var hourStr = hour.toString()
    if (hourStr.length == 1)
        hourStr = "0$hourStr"
    var minutesStr = minutes.toString()
    if (minutesStr.length == 1)
        minutesStr = "0$minutesStr"
    return "$hourStr:$minutesStr"
}

fun editTextTimeDialogInject(context: Context?, editText: EditText) {
    editText.setOnClickListener {
        val hour = 0
        val minute = 0
        val timePicker = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                editText.setText(
                    timeFormatter(selectedHour, selectedMinute)
                )
            },
            hour,
            minute,
            true
        )
        timePicker.setTitle("Select Time")
        timePicker.show()
    }
}

fun intToWeekday(day: Int, resources: Resources): String {
    val weekdays = resources.getStringArray(R.array.weekdays)
    return weekdays[day]
}

fun weekdayToInt(day: String, resources: Resources): Int {
    val weekdays = resources.getStringArray(R.array.weekdays)
    for (i in weekdays.indices)
        if (weekdays[i] == day)
            return i
    throw Exception("Invalid weekday")
}

fun editTextWeekdayDialogInject(context: Context?, editText: EditText) {
    editText.setOnClickListener {
        var selectedItem: Int? = null
        if (context == null)
            return@setOnClickListener
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set your weekday")
            .setSingleChoiceItems(
                R.array.weekdays, -1
            ) { _, which ->
                selectedItem = which
            }
            .setPositiveButton(
                "Ok"
            ) { _, _ ->
                if (selectedItem != null)
                    editText.setText(intToWeekday(selectedItem!!, context.resources))
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
                return@setNegativeButton
            }

        builder.create()
        builder.show()

    }
}

fun getBackgroundColor(activity: Activity): Int {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    return sharedPref.getInt(
        "background_color",
        ContextCompat.getColor(activity.applicationContext, R.color.secondaryColor)
    )
}

fun updateBackgroundColor(activity: Activity, color: Int) {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt("background_color", color)
        apply()
    }
}

fun setBackgroundColor(activity: Activity) {
    val bgColor = getBackgroundColor(activity)
    activity.window.setBackgroundDrawable(ColorDrawable(bgColor))
    activity.window.navigationBarColor = bgColor
}

fun getAccentColor(activity: Activity): Int {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    return sharedPref.getInt(
        "accent_color",
        ContextCompat.getColor(activity.applicationContext, R.color.primaryDarkColor)
    )
}

fun updateAccentColor(activity: Activity, color: Int) {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt("accent_color", color)
        apply()
    }
}

fun setAccentColor(activity: Activity) {
    val accentColor = getAccentColor(activity)
    (activity as MainActivity).binding.toolbar.setBackgroundColor(accentColor)
    activity.window.statusBarColor = accentColor
}

fun setButtonColor(button: Button, activity: Activity) {
    button.background = DrawableBuilder()
        .rectangle()
        .solidColor(getAccentColor(activity))
        .rounded()
        .build()
}

fun labelsToString(labels: List<Label>): String {
    if (labels.isEmpty())
        return ""
    val toReturn = StringBuilder()
    toReturn.append(labels[0].name)
    for (i in 1 until labels.size)
        toReturn.append(", ").append(labels[i].name)
    return toReturn.toString()
}

fun prettyTimeString(calendar: Calendar?, noTime: Boolean = false): String {
    if (calendar == null)
        return ""
    var str1 = calendar.get(Calendar.HOUR_OF_DAY).toString()
    if (str1.length == 1)
        str1 = "0$str1"
    var str2 = calendar.get(Calendar.MINUTE).toString()
    if (str2.length == 1)
        str2 = "0$str2"
    val monthString = mapOf(
        0 to "january",
        1 to "february",
        2 to "march",
        3 to "april",
        4 to "may",
        5 to "june",
        6 to "july",
        7 to "august",
        8 to "september",
        9 to "october",
        10 to "november",
        11 to "december"
    )[calendar.get(Calendar.MONTH)]
    if (!noTime)
        return "${calendar.get(Calendar.DAY_OF_MONTH)}-${monthString}-${
            calendar.get(
                Calendar.YEAR
            )
        } $str1:$str2"
    return "${calendar.get(Calendar.DAY_OF_MONTH)}-${monthString}-${calendar.get(Calendar.YEAR)}"
}

fun scheduleNotificationOnIO(
    context: Context,
    id: Int,
    reminder: Calendar?
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent = Intent(context, AlarmReceiver::class.java)
    notificationIntent.putExtra("id", id)
    val broadcast = PendingIntent.getBroadcast(
        context,
        id,
        notificationIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )
    if (reminder == null) {
        alarmManager.cancel(broadcast)
    } else alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        reminder.timeInMillis,
        broadcast
    )
}

fun scheduleNotification(
    context: Context,
    id: Int,
    reminder: Calendar?
) {
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    uiScope.launch {
        withContext(Dispatchers.IO) {
            scheduleNotificationOnIO(context, id, reminder)
        }
    }
}

fun scheduleDeletionOnIO(
    context: Context,
    cardId: Long,
    id: Int,
    expirationDate: Calendar?
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent = Intent(context, DeletionReceiver::class.java)
    notificationIntent.putExtra("id", id)
    notificationIntent.putExtra("cardId", cardId)
    val broadcast = PendingIntent.getBroadcast(
        context,
        id,
        notificationIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )
    if (expirationDate == null) {
        alarmManager.cancel(broadcast)
    } else alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        expirationDate.timeInMillis,
        broadcast
    )
}

fun scheduleDeletion(
    context: Context,
    cardId: Long,
    id: Int,
    expirationDate: Calendar?
) {
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    uiScope.launch {
        withContext(Dispatchers.IO) {
            scheduleDeletionOnIO(context, cardId, id, expirationDate)
        }
    }
}

suspend fun clearDataFromDatabase(context: Context, cardDatabaseDao: CardDatabaseDao) {
    for (card in cardDatabaseDao.getAllCardsNow()) {
        if (card.reminderId != null)
            scheduleNotificationOnIO(context, card.reminderId!!, null)
        if (card.expirationId != null)
            scheduleDeletionOnIO(context, card.cardId, card.expirationId!!, null)
    }
    for (subtask in cardDatabaseDao.getAllSubtasksNow())
        if (subtask.reminderId != null)
            scheduleNotificationOnIO(context, subtask.reminderId!!, null)
    cardDatabaseDao.clearCards()
    cardDatabaseDao.clearLabels()
}

suspend fun getTextFromDB(cardDatabaseDao: CardDatabaseDao): String {
    var toReturn =
        "this string is here so that you can't mistake files for the valid ones. Don't try to pass files with this string, you'll likely crash the app\n"
    val listOfLabels = cardDatabaseDao.getAllLabelsNow()
    toReturn += "labels ${listOfLabels.size}\n"
    for (label in listOfLabels) {
        toReturn += "labelId\n${label.labelId}\n"
        toReturn += "name\n${label.name}\n"
        toReturn += "visible\n${label.visible}\n"
        toReturn += "end\n"
    }
    val listOfCards = cardDatabaseDao.getAllCardsNow()
    toReturn += "cards ${listOfCards.size}\n"
    for (card in listOfCards) {
        toReturn += "cardId\n${card.cardId}\n"
        toReturn += "timeBegin\n${card.timeBegin}\n"
        toReturn += "timeEnd\n${card.timeEnd}\n"
        toReturn += "weekday\n${card.weekday}\n"
        toReturn += "place\n${card.place}\n"
        toReturn += "name\n${card.name}\n"
        toReturn += "info\n${card.info}\n"
        toReturn += "color\n${card.color}\n"
        toReturn += "textColor\n${card.textColor}\n"
        if (card.reminderDate != null)
            toReturn += "reminderDate\n${card.reminderDate!!.timeInMillis}\n"
        if (card.reminderId != null)
            toReturn += "reminderId\n${card.reminderId}\n"
        if (card.expirationDate != null)
            toReturn += "expirationDate\n${card.expirationDate!!.timeInMillis}\n"
        if (card.expirationId != null)
            toReturn += "expirationId\n${card.expirationId}\n"
        toReturn += "end\n"
    }
    val listOfCardLabels = cardDatabaseDao.getCardLabelsNow()
    toReturn += "cardLabels ${listOfCardLabels.size}\n"
    for (cardLabel in listOfCardLabels) {
        toReturn += "cardId\n${cardLabel.cardId}\n"
        toReturn += "labelId\n${cardLabel.labelId}\n"
        toReturn += "end\n"
    }
    val subtasks = cardDatabaseDao.getAllSubtasksNow()
    toReturn += "subtasks ${subtasks.size}\n"
    for (subtask in subtasks) {
        toReturn += "subtaskId\n${subtask.subtaskId}\n"
        toReturn += "cardId\n${subtask.cardId}\n"
        toReturn += "description\n${subtask.description}\n"
        if (subtask.dueDate != null)
            toReturn += "dueDate\n${subtask.dueDate!!.timeInMillis}\n"
        if (subtask.reminderDate != null)
            toReturn += "reminderDate\n${subtask.reminderDate!!.timeInMillis}\n"
        if (subtask.reminderId != null)
            toReturn += "reminderId\n${subtask.reminderId}\n"
        toReturn += "end\n"
    }
    toReturn += "end\n"
    return toReturn
}

suspend fun loadDataFromFile(fileName: Uri, context: Context): Boolean {
    val cardDatabaseDao: CardDatabaseDao = CardDatabase.getInstance(context).cardDatabaseDao
    val contentResolver = context.contentResolver
    val header =
        "this string is here so that you can't mistake files for the valid ones. Don't try to pass files with this string, you'll likely crash the app"
    val labels = mutableListOf<Label>()
    val cards = mutableListOf<Card>()
    val cardLabels = mutableListOf<CardLabel>()
    val subtasks = mutableListOf<Subtask>()
    @Suppress("BlockingMethodInNonBlockingContext")
    contentResolver.openInputStream(fileName)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            if (line != header)
                return false
            while (true) {
                val lines: List<String> = reader.readLine().split(" ")
                if (lines[0] == "end")
                    break
                when (lines[0]) {
                    "labels" -> {
                        for (i in 1..lines[1].toInt()) {
                            var labelId = 0L
                            var visible = 1
                            var name = ""
                            while (true) {
                                line = reader.readLine()
                                if (line == "end")
                                    break
                                when (line) {
                                    "labelId" -> labelId = reader.readLine().toLong()
                                    "name" -> name = reader.readLine()
                                    "visible" -> visible = reader.readLine().toInt()
                                }
                            }
                            labels.add(Label(labelId, visible, name))
                        }
                    }
                    "cards" -> {
                        for (i in 1..lines[1].toInt()) {
                            var cardId = 0L
                            var timeBegin = ""
                            var timeEnd = ""
                            var weekday = -1
                            var place = ""
                            var name = ""
                            var info = ""
                            var color = 0
                            var textColor = 0
                            var reminderDate: Calendar? = null
                            var reminderId: Int? = null
                            var expirationDate: Calendar? = null
                            var expirationId: Int? = null
                            while (true) {
                                line = reader.readLine()
                                if (line == "end")
                                    break
                                when (line) {
                                    "cardId" -> cardId = reader.readLine().toLong()
                                    "timeBegin" -> timeBegin = reader.readLine()
                                    "timeEnd" -> timeEnd = reader.readLine()
                                    "weekday" -> weekday = reader.readLine().toInt()
                                    "place" -> place = reader.readLine()
                                    "name" -> name = reader.readLine()
                                    "info" -> info = reader.readLine()
                                    "color" -> color = reader.readLine().toInt()
                                    "textColor" -> textColor = reader.readLine().toInt()
                                    "reminderDate" -> {
                                        val now = Calendar.getInstance()
                                        now.timeInMillis = reader.readLine().toLong()
                                        reminderDate = now
                                    }
                                    "reminderId" -> reminderId = reader.readLine().toInt()
                                    "expirationDate" -> {
                                        val now = Calendar.getInstance()
                                        now.timeInMillis = reader.readLine().toLong()
                                        expirationDate = now
                                    }
                                    "expirationId" -> expirationId = reader.readLine().toInt()
                                }
                            }
                            cards.add(
                                Card(
                                    cardId,
                                    timeBegin,
                                    timeEnd,
                                    weekday,
                                    place,
                                    name,
                                    info,
                                    color,
                                    textColor,
                                    reminderDate,
                                    reminderId,
                                    expirationDate,
                                    expirationId
                                )
                            )
                        }
                    }
                    "cardLabels" -> {
                        for (i in 1..lines[1].toInt()) {
                            var cardId = 0L
                            var labelId = 0L
                            while (true) {
                                line = reader.readLine()
                                if (line == "end")
                                    break
                                when (line) {
                                    "cardId" -> cardId = reader.readLine().toLong()
                                    "labelId" -> labelId = reader.readLine().toLong()
                                }
                            }
                            cardLabels.add(CardLabel(cardId, labelId))
                        }
                    }
                    "subtasks" -> {
                        for (i in 1..lines[1].toInt()) {
                            var subtaskId = 0L
                            var cardId = 0L
                            var description = ""
                            var dueDate: Calendar? = null
                            var reminderDate: Calendar? = null
                            var reminderId: Int? = null
                            while (true) {
                                line = reader.readLine()
                                if (line == "end")
                                    break
                                when (line) {
                                    "subtaskId" -> subtaskId = reader.readLine().toLong()
                                    "cardId" -> cardId = reader.readLine().toLong()
                                    "description" -> description = reader.readLine()
                                    "dueDate" -> {
                                        val now = Calendar.getInstance()
                                        now.timeInMillis = reader.readLine().toLong()
                                        dueDate = now
                                    }
                                    "reminderDate" -> {
                                        val now = Calendar.getInstance()
                                        now.timeInMillis = reader.readLine().toLong()
                                        reminderDate = now
                                    }
                                    "reminderId" -> reminderId = reader.readLine().toInt()
                                }
                            }
                            subtasks.add(
                                Subtask(
                                    subtaskId,
                                    cardId,
                                    description,
                                    dueDate,
                                    reminderDate,
                                    reminderId
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    clearDataFromDatabase(context, cardDatabaseDao)
    val nopes = mutableSetOf<Long>()
    for (label in labels)
        cardDatabaseDao.insertLabel(label)
    for (card in cards)
        if (card.expirationId == null || card.expirationDate == null || card.expirationDate!! > Calendar.getInstance()) {
            cardDatabaseDao.insertCard(card)
            if (card.reminderId != null && card.reminderDate != null)
                scheduleNotificationOnIO(context, card.reminderId!!, card.reminderDate)
            if (card.expirationId != null && card.expirationDate != null)
                scheduleDeletionOnIO(context, card.cardId, card.expirationId!!, card.expirationDate)
        } else nopes.add(card.cardId)
    for (cardLabel in cardLabels)
        if (!nopes.contains(cardLabel.cardId))
            cardDatabaseDao.connectLabelToCard(cardLabel.cardId, cardLabel.labelId)
    for (subtask in subtasks)
        if (!nopes.contains(subtask.cardId)) {
            cardDatabaseDao.insertSubtask(subtask)
            if (subtask.reminderId != null && subtask.reminderDate != null)
                scheduleNotificationOnIO(context, subtask.reminderId!!, subtask.reminderDate)
        }
    return true
}