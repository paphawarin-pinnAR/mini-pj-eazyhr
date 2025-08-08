package com.example.miniproject.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.R
import com.example.miniproject.data.model.DayData
import com.example.miniproject.listener.OnDayClickListener

class DayAdapter (
    private val context: Context,
    private val days: List<DayData>,
    private val listener: OnDayClickListener )
    : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

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

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val rvLineDivider: View = itemView.findViewById(R.id.rvLine_divider)

        fun bind(day: DayData) {
            tvDay.text = day.date.dayOfMonth.toString()

            val color = when {
                day.isToday -> Color.BLACK
                day.isWeekend -> Color.GRAY
                else -> Color.BLACK
            }

            tvDay.setTextColor(color)

            tvDay.setOnClickListener {
                listener.onDayClick(day)
            }
        }

    }

}