package com.example.miniproject.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.R
import com.example.miniproject.data.model.DayData
import com.example.miniproject.listener.OnDayClickListener
import java.time.LocalDate

class DayAdapter (
    private val context: Context,
    private val days: List<DayData?>,
    private val listener: OnDayClickListener,
    private var selectedDay: LocalDate? = null
): RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    private fun findDayIndex(date: LocalDate?): Int {
        return days.indexOfFirst { it?.date == date }
    }

    fun updateDayChange(date: LocalDate?) {
        //for notice the RecyclerView that which the items need to refresh
        val previousDayIndex = findDayIndex(selectedDay)
        val newDayIndex = findDayIndex(date)

        //update the selectedDate as the user's selection
        selectedDay = date

        if (previousDayIndex != -1) notifyItemChanged(previousDayIndex)
        if (newDayIndex != -1) notifyItemChanged(newDayIndex)
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val mkToday: ImageView = itemView.findViewById(R.id.markerToday)
        private val mkDaySelect: ImageView = itemView.findViewById(R.id.markerDaySelected)

        fun bind(day: DayData?) {
            if (day == null) {
                setupDayHide()
                return
            }
            setupDayDisplay(day)
            setupMarkerDisplay(day)
            setupDayClick(day)
        }

        private fun setupDayHide() {
            tvDay.visibility = View.INVISIBLE
            mkToday.visibility = View.GONE
            mkDaySelect.visibility = View.GONE
            return
        }

        private fun setupDayDisplay(day: DayData) {
            tvDay.visibility = View.VISIBLE
            tvDay.text = day.date.dayOfMonth.toString()

            val color = when {
                day.isToday && day.date == selectedDay -> Color.WHITE
                day.date == selectedDay -> Color.WHITE
                day.isWeekend -> Color.GRAY
                else -> Color.BLACK
            }
            tvDay.setTextColor(color)
        }

        private fun setupMarkerDisplay(day: DayData) {
            mkToday.visibility = if (day.isToday) View.VISIBLE else View.GONE
            mkDaySelect.visibility = if (day.date == selectedDay) View.VISIBLE else View.GONE
        }

        private fun setupDayClick(day: DayData) {
            tvDay.setOnClickListener {
                val previousSelected = selectedDay
                selectedDay = day.date

                updateDayChange(previousSelected)
                updateDayChange(selectedDay)

                listener.onDayClick(day)
            }
        }
    }
}