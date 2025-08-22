package com.example.miniproject.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    //Date format
    fun getDateFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    }

    //Time format
    fun getTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    }

    //LocalDate -> String
    fun formatDate(date: LocalDate): String {
        return date.format(getDateFormatter())
    }

    //LocalTime -> String
    fun formatTime(time: LocalTime): String {
        return time.format(getTimeFormatter())
    }

    //LocalDateTime -> Pair(Date, Time)
    fun formatDateTime(dateTime: LocalDateTime): Pair<String, String> {
        return dateTime.format(getDateFormatter()) to dateTime.format(getTimeFormatter())
    }

    // String -> LocalTime object
    fun parseTimeUser(timeStr: String): LocalTime {
        return LocalTime.parse(timeStr, getTimeFormatter())
    }

    fun parseDateUser(dateStr: String): LocalDate {
        return LocalDate.parse(dateStr, getDateFormatter())
    }

    //Millis -> Time
    fun formatTimeFromMillis(millis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun parseTimeOrNull(timeStr: String): LocalTime? {
        return try {
            LocalTime.parse(timeStr, getTimeFormatter())
        } catch (e: Exception) {
            null
        }
    }

}