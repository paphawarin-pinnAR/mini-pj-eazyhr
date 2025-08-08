package com.example.miniproject.data.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class DayData(
    val date: LocalDate,
    val isToday: Boolean,
    val isWeekend: Boolean
)

object DayDataSource {

    private val today = LocalDate.now()

    fun generateDaysInMonth(year: Int, month: Int): List<DayData> {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        return (1..daysInMonth).map { day ->
            val date = LocalDate.of(year, month, day)
            DayData(
                date = date,
                isToday = date == today,
                isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            )
        }
    }
}