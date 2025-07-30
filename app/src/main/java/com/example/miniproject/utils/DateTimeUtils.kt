package com.example.miniproject.utils

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    val displayDate: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    val displayTime: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    fun formatTimeFromMillis(millis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}