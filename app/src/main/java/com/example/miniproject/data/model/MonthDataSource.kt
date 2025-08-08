package com.example.miniproject.data.model

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class MonthData(
    val year: Int,
    val monthValue : Int,
    val monthName: String,
    val days: List<DayData>
)

// object is the singleton -> สร้างได้ครั้งเดียว ใช้ได้ทั้งแอป //แหล่งข้อมูลของเดือน (data provider)
object MonthDataSource {

    private val today = LocalDate.now()

    fun getMonthList(year: Int = today.year): List<MonthData> {
        return (1..12).map { month ->
            val days = DayDataSource.generateDaysInMonth(year, month)
            val monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            MonthData(
                year = year,
                monthValue = month,
                monthName = monthName,
                days = days

            )
        }
    }
}