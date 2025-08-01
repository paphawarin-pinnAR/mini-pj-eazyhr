import android.util.Log
import com.example.miniproject.data.model.ApiResponse
import com.example.miniproject.data.model.ClockInData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.data.model.ClockOutData
import com.example.miniproject.data.model.ClockOutRequest
import com.example.miniproject.data.model.LeaveRequest
import com.example.miniproject.data.network.ApiClient
import com.example.miniproject.ui.view.HrView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HrController(private val view: HrView, private val repository: HrRepository) {

    // private val repository: HrRepository = HrRepository(ApiClient.apiService)
    private val controllerScope = CoroutineScope(Dispatchers.Main)

    fun clockInUser(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                //เรียก Repository (repository.clockIn(userId)) ที่ใช้ Retrofit ในการยิง API ไปยัง server
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

    fun clockOutUser(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val clockOutData = repository.clockOut(ClockOutRequest(userId))
                if (clockOutData != null) {
                    view.onClockOutSuccess(clockOutData.clockOutTime)
                } else {
                    view.onError("การลงเวลาออกงานล้มเหลว")
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

    fun getTodayClockIn(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val attendanceList = repository.getTodayAttendance()
                val userAttendance = attendanceList?.firstOrNull()
                if (userAttendance != null) {
                    view.onClockInSuccess(userAttendance.clockInTime)
                } else {
                    view.onNoAttendanceData()
                }
            } catch (e: Exception) {
                view.onError("โหลดข้อมูลล้มเหลว: ${e.message}")
            }
        }
    }

    fun getTodayClockOut(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val attendanceList = repository.getTodayAttendance()
                val userAttendance = attendanceList?.firstOrNull()
                if (userAttendance != null) {
                    view.onClockOutSuccess(userAttendance.clockOutTime)
                } else {
                    view.onNoAttendanceData()
                }
            } catch (e: Exception) {
                view.onError("โหลดข้อมูลล้มเหลว: ${e.message}")
            }
        }
    }

    fun applyForLeave(leaveRequest: LeaveRequest) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val response = repository.applyForLeave(leaveRequest)

                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        view.onLeaveApplicationSuccess(response.data)
                    } else {
                        view.onError("การยื่นใบลาล้มเหลว: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.onError("ข้อผิดพลาดเครือข่าย: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    view.showLoading(false)
                }
            }
        }
    }
}

