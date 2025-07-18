package com.example.miniproject.utils

import java.time.format.DateTimeFormatter
import java.util.Locale

object AppFormatters {
    val displayDate: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    val displayTime: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

}