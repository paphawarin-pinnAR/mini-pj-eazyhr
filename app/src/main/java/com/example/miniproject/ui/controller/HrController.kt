import android.util.Log
import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.ui.view.HrView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HrController(private val view: HrView) {

    private val repository: HrRepository = HrRepository()
    private val controllerScope = CoroutineScope(Dispatchers.Main)

    fun clockInUser(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val clockInData = repository.clockIn(ClockInRequest(userId))
                if (clockInData != null) {
                    view.onClockInSuccess(clockInData.clockInTime)
                } else {
                    view.onError("การลงเวลาเข้างานล้มเหลว")
                }
            } catch (e: Exception) {
                view.onError("ข้อผิดพลาดเครือข่าย: ${e.message}")
            } finally {
                view.showLoading(false)
            }
        }
    }

    fun loadUserData(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val response = repository.getUserById(userId)
                if (response.status == "success") {
                    view.displayUserData(response.data)
                    Log.d("UserData", "DATA: ${Gson().toJson(response.data)}")
                } else {
                    view.onError("โหลดข้อมูลผู้ใช้ล้มเหลว: ${response.message}")
                }
            } catch (e: Exception) {
                view.onError("ข้อผิดพลาดเครือข่าย: ${e.message}")
            } finally {
                view.showLoading(false)
            }
        }
    }

}