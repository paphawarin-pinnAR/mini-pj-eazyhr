import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.data.model.User
import com.example.miniproject.data.network.ApiClient
import com.example.miniproject.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HrRepository {
    private val apiService = ApiClient.instance.create(ApiService::class.java)

    suspend fun getUserById(userId: Long): ApiResponse<User> {
        return withContext(Dispatchers.IO) {
            apiService.getUserById(userId)
        }
    }

    suspend fun clockIn(request: ClockInRequest): ClockInData? {
        return try {
            val response = apiService.clockIn(userId = request.userId.toInt())
            if (response.status == "success") response.data else null
        } catch (e: Exception) {
            null
        }
    }



}


