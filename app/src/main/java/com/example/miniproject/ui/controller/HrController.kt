import android.content.Context
import android.util.Log
import com.example.miniproject.R
import com.example.miniproject.constants.ApiStatus
import com.example.miniproject.data.model.AttendanceData
import com.example.miniproject.data.model.ClockInRequest
import com.example.miniproject.data.model.ClockOutRequest
import com.example.miniproject.data.model.LeaveRequest
import com.example.miniproject.ui.view.HrView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HrController(private val view: HrView, private val repository: HrRepository, private val context: Context) {

    private val controllerScope = CoroutineScope(Dispatchers.Main)
    private var cachedAttendance: AttendanceData? = null

    fun clockInUser(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                //เรียก Repository (repository.clockIn(userId)) ที่ใช้ Retrofit ในการยิง API ไปยัง server
                val clockInData = repository.clockIn(ClockInRequest(userId))
                if (clockInData != null) {
                    view.onClockInSuccess(clockInData.clockInTime)
                } else {
                    view.onError(context.getString(R.string.error_clock_in_failed))
                }
            } catch (e: Exception) {
                view.onError(context.getString(R.string.error_network, e.message))
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
                    view.onError(context.getString(R.string.error_clock_out_failed))
                }
            } catch (e: Exception) {
                view.onError(context.getString(R.string.error_network, e.message))
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
                if (response.status == ApiStatus.SUCCESS) {
                    view.displayUserData(response.data)
                    Log.d("UserData", "DATA: ${Gson().toJson(response.data)}")
                } else {
                    view.onError(context.getString(R.string.error_load_user_failed, response.message))
                }
            } catch (e: Exception) {
                view.onError(context.getString(R.string.error_network, e.message))
            } finally {
                view.showLoading(false)
            }
        }
    }

    fun getTodayClockIn(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                // if there is no cached data, retrieve attendance data from the repository
                if (cachedAttendance == null) {
                    val attendanceList = repository.getTodayAttendance()
                    cachedAttendance = attendanceList?.firstOrNull()
                }
                if (cachedAttendance != null) {
                    val userAttendance = cachedAttendance
                    if (userAttendance != null) {
                        view.onClockInSuccess(userAttendance.clockInTime)
                    } else {
                        view.onNoAttendanceData()
                    }
                }
            } catch (e: Exception) {
                view.onError(context.getString(R.string.error_load_data_failed, e.message))
            }
        }
    }

    fun getTodayClockOut(userId: Long) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                if (cachedAttendance == null) {
                    val attendanceList = repository.getTodayAttendance()
                    cachedAttendance = attendanceList?.firstOrNull()
                }
                if (cachedAttendance != null) {
                    val userAttendance = cachedAttendance
                    if (userAttendance != null) {
                        view.onClockOutSuccess(userAttendance.clockOutTime)
                    } else {
                        view.onNoAttendanceData()
                    }
                }
            } catch (e: Exception) {
                view.onError(context.getString(R.string.error_load_data_failed, e.message))
            }
        }
    }

    fun applyForLeave(leaveRequest: LeaveRequest) {
        controllerScope.launch {
            view.showLoading(true)
            try {
                val response = repository.applyForLeave(leaveRequest)
                withContext(Dispatchers.Main) {
                    if (response.status == ApiStatus.SUCCESS) {
                        view.onLeaveApplicationSuccess(response.data)
                    } else {
                        view.onError(context.getString(R.string.error_leave_failed, response.message))

                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.onError(context.getString(R.string.error_network, e.message))
                }
            } finally {
                withContext(Dispatchers.Main) {
                    view.showLoading(false)
                }
            }
        }
    }
}

