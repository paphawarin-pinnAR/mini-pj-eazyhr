package com.example.miniproject.data.model

data class LeaveRequest(
    val userId: Long,
    val leaveCategory: String,
    val leavePeriod: String,
    val startDate: Long,
    val endDate: Long,
    val reason: String,
    val totalDays: Double
)
