package com.example.miniproject.ui.view.activity

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
import com.example.miniproject.data.model.DayData
import com.example.miniproject.data.model.MonthDataSource
import com.example.miniproject.listener.OnDayClickListener
import com.example.miniproject.ui.adapter.MonthAdapter
import com.example.miniproject.utils.ToastUtils
import java.time.format.DateTimeFormatter
import java.util.Locale

class SecondActivity : AppCompatActivity(), OnDayClickListener{

    private lateinit var btnPrevious : ImageView
    private lateinit var monthAdapter: MonthAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        initViews()
        setupButtonListener()
        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        btnPrevious = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.calendarRecyclerView)
    }

    private fun setupRecyclerView(){
        val monthList = MonthDataSource.getMonthList()
        monthAdapter = MonthAdapter(this, monthList, this)

        recyclerView.apply {
            // จัดเรียง item แบบแนวตั้งเป็น list ปกติ (scroll แนวตั้ง) //ส่ง Context ของ Activity ที่เราอยู่ ให้กับ LinearLayoutManager
            // Context นี้จำเป็นสำหรับสร้าง LayoutManager
            layoutManager = LinearLayoutManager(this@SecondActivity)
            adapter = monthAdapter
            setHasFixedSize(true) // ใช้เมื่อข้อมูลใน RecyclerView มีขนาดคงที่
        }
    }

    private fun setupButtonListener(){
        btnPrevious.setOnClickListener {
            finish()
        }
    }

    override fun onDayClick(day: DayData) {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        Toast.makeText(this,"คุณเลือกวันที่ ${day.date.format(formatter)}", Toast.LENGTH_SHORT).show()
    }
}