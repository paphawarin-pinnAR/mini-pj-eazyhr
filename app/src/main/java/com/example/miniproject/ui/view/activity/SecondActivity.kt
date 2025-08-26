package com.example.miniproject.ui.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.data.model.DayData
import com.example.miniproject.data.model.MonthDataSource
import com.example.miniproject.listener.OnDayClickListener
import com.example.miniproject.ui.adapter.MonthAdapter
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class SecondActivity : AppCompatActivity(), OnDayClickListener{

    private lateinit var btnPrevious : ImageView
    private lateinit var monthAdapter: MonthAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnSubmit : Button

    private var selectedDay: DayData? = null
    private var preSelectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        preSelectedDate = getPreSelectedDateFromIntent()
        initViews()
        setupButtonListener()
        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getPreSelectedDateFromIntent(): LocalDate? {
        val dateString = intent.getStringExtra(AppConstants.SELECTED_DATE_REQ) ?: return null
        return try {
            LocalDate.parse(dateString, DateTimeUtils.getDateFormatter())
        } catch (e: Exception) {
            null
        }
    }

    private fun initViews() {
        btnPrevious = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.calendarRecyclerView)
        btnSubmit = findViewById(R.id.submit_button)
    }

    private fun setupRecyclerView() {
        val monthList: List<YearMonth> = MonthDataSource.getYearMonthList()
        monthAdapter = MonthAdapter(this, monthList, this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SecondActivity)
            adapter = monthAdapter
            setHasFixedSize(true)
        }

        recyclerView.post {
            val today = preSelectedDate ?: LocalDate.now()
            val monthIndex = monthList.indexOfFirst { it.year == today.year && it.monthValue == today.monthValue }
            if (monthIndex != -1) {
                monthAdapter.selectDay(monthIndex, today)

                //set the selectedDay
                selectedDay = DayData(
                    date = today,
                    isToday = today == LocalDate.now(),
                    isWeekend = today.dayOfWeek.value >= 6
                )

                //scroll to the current month
                recyclerView.scrollToPosition(monthIndex)
            }
        }
    }

    private fun setupButtonListener(){
        btnPrevious.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            selectedDay?.let { day ->
                val formatter = DateTimeUtils.getDateFormatter()
                val selectedDate = day.date.format(formatter)

                //send the selected date to the Fragment
                val result = Intent().apply {
                    putExtra(AppConstants.SELECTED_DATE_REQ, selectedDate)
                }

                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
    }

    override fun onDayClick(day: DayData) {
        selectedDay = day
    }
}