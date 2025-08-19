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
import com.example.miniproject.data.model.MonthData
import com.example.miniproject.listener.OnDayClickListener
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.time.LocalDate

class MonthAdapter (
    private val context: Context,
    private val months: List<MonthData> ,
    private val dayClickListener: OnDayClickListener)
    : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    private val dayAdapters = mutableMapOf<Int, DayAdapter>()  //To store tha DayAdapter for all months

    private var previousSelectedMonth: Int? = null
    private var previousSelectedDate: LocalDate? = null

    //Create the ViewHolder for each item_month.xml //use LayoutInflater to convert XML to View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return MonthViewHolder(view)
    }

    override fun getItemCount(): Int {
        return months.size
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = months[position]
        holder.bind(month, position)
    }

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val rvDays: RecyclerView = itemView.findViewById(R.id.rvDays)

        fun bind(month: MonthData, position: Int) {
            setupMonthName(month)
            setupDaysRecyclerView(month, position)
        }

        private fun setupMonthName(month: MonthData){
            tvMonth.text = month.monthName
        }

        private fun setupDaysRecyclerView(month: MonthData, position: Int) {
            rvDays.layoutManager = GridLayoutManager(context, 7)
            rvDays.setHasFixedSize(true)

            //Use the Map (dayAdapters) to store the DayAdapter of each month by the position.
            //If it already exists, retrieve and use it.  // If not, create a new one and map it.
            val adapter = dayAdapters.getOrPut(position) {
                DayAdapter(
                    context,
                    month.days.toMutableList(),
                    createDayClickListener(position)
                )
            }

            rvDays.adapter = adapter
        }

        private fun createDayClickListener(monthIndex: Int): OnDayClickListener {
            //When user press the Day in the Month -> Retrieve the onDayClick() -> Retrieve the handleDaySelection()
            return object : OnDayClickListener {
                override fun onDayClick(day: DayData) {
                    handleDaySelection(monthIndex, day)
                }
            }
        }

        private fun handleDaySelection(currentMonth: Int, day: DayData) {
            //Reset the Green marker //.let is a scope function that allows you to use the value of a variable within a { ... } block.
            previousSelectedMonth?.let { previousMonthIndex ->
                dayAdapters[previousMonthIndex]?.updateDayChange(null)
            }

            //Set the selected date
            dayAdapters[currentMonth]?.updateDayChange(day.date)

            //Save selected month and date data
            previousSelectedMonth = currentMonth
            previousSelectedDate = day.date

            //Send event back
            dayClickListener.onDayClick(day)
        }
    }
}

