package com.flaviu.timetable

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.flaviu.timetable.database.Card

@BindingAdapter("timeText")
fun TextView.setTimeText(item: Card?) {
    item?.let{
        var begin = item.timeBegin.toString()
        var end = item.timeEnd.toString()
        if (begin.length == 1)
            begin = "0$begin"
        if (end.length == 1)
            end = "0$end"
        text="Time: $begin - $end"
    }
}

@BindingAdapter("placeText")
fun TextView.setPlaceText(item: Card?) {
    item?.let{
        text="Place: ${item.place}"
    }
}

@BindingAdapter("nameText")
fun TextView.setNameText(item: Card?) {
    item?.let{
        text="Name: ${item.name}"
    }
}

@BindingAdapter("infoText")
fun TextView.setInfoText(item: Card?) {
    item?.let{
        text="Description: ${item.info}"
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
        setText(intToWeekday(item.weekday))
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

@BindingAdapter("labelText")
fun EditText.setLabelText(item: Card?) {
    item?.let {
        setText(item.label)
    }
}