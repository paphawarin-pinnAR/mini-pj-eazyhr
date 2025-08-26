package com.example.miniproject.data.network

import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.AttendanceData
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockOutData
import com.example.miniproject.data.model.LeaveData
import com.example.miniproject.data.model.LeaveRequest
import com.example.miniproject.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): ApiResponse<User>

    @POST("api/attendance/clock-in")
    suspend fun clockIn(@Query("userId") userId: Int): ApiResponse<ClockInData>

    @POST("api/attendance/clock-out")
    suspend fun clockOut(@Query("userId") userId: Int): ApiResponse<ClockOutData>

    @GET("api/attendance/today")
    suspend fun getTodayAttendance(): ApiResponse<List<AttendanceData>>

    @POST("api/leave/apply")
    suspend fun applyLeave(@Body request: LeaveRequest): ApiResponse<LeaveData>
}
