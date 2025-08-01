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

data class ClockOutData(
    val id: Int,
    val totalHours: Int,
    val clockOutTime: Long,
    val attendanceDate: Long
)

data class AttendanceData(
    val id: Int,
    val attendanceDate: Long,
    val clockInTime: Long,
    val clockOutTime: Long?,
    val breakDurationMinutes: Int,
    val totalHours: Double?,
    val overtimeHours: Int,
    val status: String,
    val note: String?,
    val createdAt: String,
    val updatedAt: String,
    val formattedWorkingHours: String,
    val clockedIn: Boolean,
    val clockedOut: Boolean
)

data class LeaveData (
    val id: Int,
    val leaveCategory: String,
    val startDate: Long,
    val endDate: Long,
    val leavePeriod: String,
    val totalDays: Int,
    val reason: String,
    val status: String,
    val appliedDate: String,
    val approvedBy: String?,
    val approvedDate: String?,
    val comments: String?,
    val createdAt: String,
    val updatedAt: String,
    val statusBadgeClass: String,
    val formattedDateRange: String,
    val approved: Boolean,
    val rejected: Boolean,
    val pending: Boolean
)