import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.AttendanceData
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.data.model.ClockOutData
import com.example.miniproject.data.model.ClockOutRequest
import com.example.miniproject.data.model.LeaveData
import com.example.miniproject.data.model.LeaveRequest
import com.example.miniproject.data.model.User
import com.example.miniproject.data.network.ApiClient
import com.example.miniproject.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HrRepository (private val apiService: ApiService) {
   // private val apiService = ApiClient.instance.create(ApiService::class.java)

    suspend fun getUserById(userId: Long): ApiResponse<User> {
        return withContext(Dispatchers.IO) {
            apiService.getUserById(userId)
        }
    }

    suspend fun clockIn(request: ClockInRequest): ClockInData? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.clockIn(request.userId.toInt())
                if (response.status == "success") response.data else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun clockOut(request: ClockOutRequest): ClockOutData? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.clockOut(request.userId.toInt())
                if (response.status == "success") response.data else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getTodayAttendance(): List<AttendanceData>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTodayAttendance()
                if (response.status == "success") response.data else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun applyForLeave(leaveRequest: LeaveRequest): ApiResponse<LeaveData> {
        return withContext(Dispatchers.IO) {
            apiService.applyLeave(leaveRequest)
        }
    }
}


