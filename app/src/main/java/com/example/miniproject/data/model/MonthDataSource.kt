package com.example.miniproject.data.model

import java.time.YearMonth

//object is the singleton
object MonthDataSource {
    fun getYearMonthList(): List<YearMonth> {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusYears(10)
        val endMonth = currentMonth.plusYears(10)

        return generateSequence(startMonth) { it.plusMonths(1) }
            .takeWhile { it <= endMonth }
            .toList()
    }
}