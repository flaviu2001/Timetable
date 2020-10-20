package com.flaviu.timetable

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

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
    0xFF009688, // TEAL 500
    0xFF4CAF50, // GREEN 500
    0xFF8BC34A, // LIGHT GREEN 500
    0xFFCDDC39, // LIME 500
    0xFFFF9800, // ORANGE 500
    0xFFFFC107, // AMBER 500
    0xFFFFEB3B, // YELLOW 500
    0xFFFFFFFF, // My white
).map{it.toInt()}.toIntArray()

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
    editText.setOnClickListener{
        val hour = 0
        val minute = 0
        val timePicker: TimePickerDialog
        timePicker = TimePickerDialog(
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
    editText.setOnClickListener{
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