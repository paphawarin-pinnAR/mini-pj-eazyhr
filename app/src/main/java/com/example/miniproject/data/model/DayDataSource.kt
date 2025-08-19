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

    fun generateDaysInMonth(year: Int, month: Int): List<DayData?> {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val firstDayInMonth = LocalDate.of(year, month, 1)
        val firstWeekDayValue = firstDayInMonth.dayOfWeek.value    //mon=1, tue=2, ..

        val days = mutableListOf<DayData?>()

        repeat(firstWeekDayValue - 1) {
            days.add(null)
        }

        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            days.add(
                DayData(
                    date = date,
                    isToday = date == today,
                    isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            ))
        }

        return days
    }
}