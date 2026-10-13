package com.example.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FormattedTime(
    val hours: String,
    val minutes: String,
    val seconds: String,
    val amPm: String,
    val fullTime: String,
    val dayOfWeek: String,
    val dateMonth: String,
    val fullDate: String,
    val secondsFraction: Float // 0.0f to 1.0f for seconds progress
)

object TimeFormatHelper {
    fun format(timeMillis: Long, is24Hour: Boolean): FormattedTime {
        val date = Date(timeMillis)

        val hourFormat = SimpleDateFormat(if (is24Hour) "HH" else "hh", Locale.getDefault())
        val minFormat = SimpleDateFormat("mm", Locale.getDefault())
        val secFormat = SimpleDateFormat("ss", Locale.getDefault())
        val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateMonthFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

        val hours = hourFormat.format(date).let { if (!is24Hour && it.startsWith("0")) it.substring(1) else it }
        val minutes = minFormat.format(date)
        val seconds = secFormat.format(date)
        val amPm = if (is24Hour) "" else amPmFormat.format(date)
        val fullTime = if (is24Hour) "$hours:$minutes" else "$hours:$minutes"
        val secInt = seconds.toIntOrNull() ?: 0

        return FormattedTime(
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            amPm = amPm,
            fullTime = fullTime,
            dayOfWeek = dayFormat.format(date),
            dateMonth = dateMonthFormat.format(date),
            fullDate = fullDateFormat.format(date),
            secondsFraction = secInt / 60f
        )
    }
}
