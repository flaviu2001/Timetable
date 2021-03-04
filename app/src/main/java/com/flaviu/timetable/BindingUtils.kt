package com.flaviu.timetable

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.Label
import com.flaviu.timetable.database.Subtask
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("timeText")
fun TextView.setTimeText(item: Card?) {
    item?.let{
        var begin = item.timeBegin
        var end = item.timeEnd
        if (begin.length == 1)
            begin = "0$begin"
        if (end.length == 1)
            end = "0$end"
        text=this.context.resources.getString(R.string.time_parser).format(begin, end)
        setTextColor(item.textColor)
    }
}

@BindingAdapter("placeText")
fun TextView.setPlaceText(item: Card?) {
    item?.let{
        text=this.context.resources.getString(R.string.place_parser).format(item.place)
        setTextColor(item.textColor)
    }
}

@BindingAdapter("nameText")
fun TextView.setNameText(item: Card?) {
    item?.let{
        text=this.context.resources.getString(R.string.name_parser).format(item.name)
        setTextColor(item.textColor)
    }
}

@BindingAdapter("infoText")
fun TextView.setInfoText(item: Card?) {
    item?.let{
        text=this.context.resources.getString(R.string.info_parser).format(item.info)
        setTextColor(item.textColor)
    }
}

@BindingAdapter("weekdaysText")
fun TextView.setWeekdaysText(item: Card?) {
    item?.let {
        text = context.getString(R.string.weekday_format).format(resources.getStringArray(R.array.weekdays)[item.weekday])
        setTextColor(item.textColor)
    }
}

@BindingAdapter("beginTimeText")
fun EditText.setBeginTimeText(item: Card?) {
    item?.let{
        val begin = item.timeBegin
        setText(begin)
    }
}

@BindingAdapter("endTimeText")
fun EditText.setEndTimeText(item: Card?) {
    item?.let{
        val begin = item.timeEnd
        setText(begin)
    }
}

@BindingAdapter("weekdayText")
fun EditText.setWeekdayText(item: Card?) {
    item?.let{
        setText(intToWeekday(item.weekday, this.resources))
    }
}

@BindingAdapter("placeText")
fun EditText.setPlaceText(item: Card?) {
    item?.let{
        setText(item.place)
    }
}

@BindingAdapter("nameText")
fun EditText.setNameText(item: Card?) {
    item?.let{
        setText(item.name)
    }
}

@BindingAdapter("infoText")
fun EditText.setInfoText(item: Card?) {
    item?.let{
        setText(item.info)
    }
}

@BindingAdapter("customBackgroundColor")
fun CardView.setCustomBackgroundColor(item: Card?) {
    item?.let {
        setCardBackgroundColor(item.color)
    }
}

@BindingAdapter("subtaskDescription")
fun TextView.setSubtaskDescription(item: Subtask?) {
    item?.let {
        text = resources.getString(R.string.subtask_description).format(item.description)
    }
}

@BindingAdapter("subtaskTextColor")
fun TextView.setSubtaskTextColor(item: Card?) {
    item?.let {
        setTextColor(item.textColor)
    }
}

@BindingAdapter("dueDate")
fun TextView.setDueDate(item: Subtask?) {
    if (item == null)
        return
    text = if (item.dueDate == null)
        context.getString(R.string.unset_format)
    else context.getString(R.string.due_date_format).format(SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(Date(item.dueDate!!.timeInMillis)))
}

@BindingAdapter("reminderText")
fun TextView.setReminderText(item: Card?) {
    if (item?.reminderDate == null || item.reminderDate!! < Calendar.getInstance())
        visibility = TextView.GONE
    else {
        visibility = TextView.VISIBLE
        text = context.getString(R.string.reminder_format).format(SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(Date(item.reminderDate!!.timeInMillis)))
    }
}

@BindingAdapter("labelVisibility")
fun ImageView.setLabelVisibility(item: Label?) {
    item?.let {
        if (item.visible == 1)
            setImageResource(R.drawable.baseline_visibility_24)
        else setImageResource(R.drawable.baseline_visibility_off_24)
    }
}