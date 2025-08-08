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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return MonthViewHolder(view)
        // สร้าง ViewHolder สำหรับแต่ละ item_month.xml //ใช้ LayoutInflater เพื่อแปลง XML -> View
    }

    override fun getItemCount(): Int {
        return months.size
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = months[position]
        holder.bind(month)
    }

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val rvDays: RecyclerView = itemView.findViewById(R.id.rvDays)

        fun bind(month: MonthData) {
            tvMonth.text = month.monthName
            rvDays.layoutManager = GridLayoutManager(context, 7)
            rvDays.adapter = DayAdapter(context, month.days, dayClickListener)
            rvDays.setHasFixedSize(true)
        }
    }
}


