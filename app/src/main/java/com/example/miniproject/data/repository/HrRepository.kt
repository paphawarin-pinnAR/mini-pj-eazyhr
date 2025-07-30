import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.AttendanceData
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.data.model.ClockOutData
import com.example.miniproject.data.model.ClockOutRequest
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
        return try {
            val response = apiService.clockIn(userId = request.userId.toInt())
            if (response.status == "success") {
                return response.data
            }

            else {
                return null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clockOut(request: ClockOutRequest): ClockOutData? {
        return try {
            val response = apiService.clockOut(userId = request.userId.toInt())
            if (response.status == "success") response.data else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTodayAttendance(): List<AttendanceData>?{
        val response = apiService.getTodayAttendance()
        return try {
            if (response.status == "success") response.data else null
        } catch (e: Exception) {
            null
        }
    }
}


