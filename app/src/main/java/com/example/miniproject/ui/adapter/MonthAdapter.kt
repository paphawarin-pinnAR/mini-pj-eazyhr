package com.example.miniproject.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.R
import com.example.miniproject.data.model.DayData
import com.example.miniproject.data.model.DayDataSource
import com.example.miniproject.listener.OnDayClickListener
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.time.LocalDate
import java.time.format.TextStyle

class MonthAdapter (
    private val context: Context,
    private val months: List<YearMonth> ,
    private val dayClickListener: OnDayClickListener,
    private val lastSelectedDate: LocalDate? = null
): RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    private val dayAdapters = mutableMapOf<Int, DayAdapter>()  //store tha DayAdapter for all months

    private var selectedDay: LocalDate? = lastSelectedDate
    private var selectedMonthIndex: Int? = null

    //create the ViewHolder for each item_month.xml //use LayoutInflater to convert XML to View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return MonthViewHolder(view)
    }

    override fun getItemCount(): Int {
        return months.size
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val yearMonth = months[position]
        holder.bind(yearMonth, position)
    }

    fun selectDay(monthIndex: Int, date: LocalDate?) {
        selectedMonthIndex?.let { oldIndex ->
            dayAdapters[oldIndex]?.updateDayChange(null)
        }
        date?.let {
            dayAdapters[monthIndex]?.updateDayChange(it)
        }
        selectedMonthIndex = monthIndex
        selectedDay = date
    }

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val rvDays: RecyclerView = itemView.findViewById(R.id.rvDays)

        fun bind(yearMonth: YearMonth, position: Int) {
            setupMonthName(yearMonth)
            setupDaysRecyclerView(yearMonth, position)
        }

        private fun setupMonthName(yearMonth: YearMonth){
            //about the full month name in English
            val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            tvMonth.text = context.getString(R.string.month_year, monthName, yearMonth.year)
        }

        private fun setupDaysRecyclerView(yearMonth: YearMonth, position: Int) {
            rvDays.layoutManager = GridLayoutManager(context, 7)
            rvDays.setHasFixedSize(true)

            //generate a list of DayData for the specified month and year using DayDataSource
            val days = DayDataSource.generateDaysInMonth(yearMonth.year, yearMonth.monthValue)

            val adapter = DayAdapter(context, days.toMutableList(), createDayClickListener(position), selectedDay)
            dayAdapters[position] = adapter
            rvDays.adapter = adapter
        }

        private fun createDayClickListener(monthIndex: Int): OnDayClickListener {
            //create a click listener for days in the month.
            //when a user click any day, onDayClick() is triggered,
            return object : OnDayClickListener {
                override fun onDayClick(day: DayData) {
                    handleDaySelection(monthIndex, day)
                }
            }
        }

        private fun handleDaySelection(monthIndex: Int, day: DayData) {
            //update prev day marker
            selectedMonthIndex?.let { previousMonthIndex ->
                dayAdapters[previousMonthIndex]?.updateDayChange(null)
            }
            //set new day marker
            dayAdapters[monthIndex]?.updateDayChange(day.date)

            //store the selected month to reset it when a new selection occurs
            selectedMonthIndex = monthIndex
            selectedDay = day.date

            dayClickListener.onDayClick(day)
        }
    }
}

