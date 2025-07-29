package com.example.miniproject.data.model

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)

data class ClockInData(
    val id: Int,
    val clockInTime: Long,
    val attendanceDate: Long
)
