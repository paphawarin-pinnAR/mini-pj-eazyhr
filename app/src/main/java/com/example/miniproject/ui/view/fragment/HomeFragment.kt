package com.example.miniproject.ui.view.fragment

import HrController
import HrRepository
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.data.model.LeaveData
import com.example.miniproject.data.model.User
import com.example.miniproject.data.network.ApiClient
import com.example.miniproject.ui.view.HrView
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.LocalDateTime
import java.time.LocalTime


class HomeFragment : Fragment(), HrView {
    lateinit var btnCheckInOut : Button
    lateinit var timeCheckIn : TextView
    lateinit var timeCheckOut :TextView
    private lateinit var controller: HrController

    var isCheckedIn: Boolean = false   //button state //false=check-out, true=check-in
    var timeCheckedIn: String? = null
    var timeCheckedOut: String? = null
    var date: String? = null
    var userId: Long = 1L              // กำหนด userId ชั่วคราว

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        // 1st is a layout design that I made, 2nd is the container, which is the object of the view group class here
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val repository = HrRepository(ApiClient.apiService)

        // สร้าง controller โดยส่ง reference ของ activity นี้ (view) ไปด้วย
        // this คือ HomeFragment ที่ implement HrView
        controller = HrController(this, repository)

        // Initialize views
        initViews(view)

        setupButtonListeners()

        // โหลดข้อมูล user จาก API
        controller.loadUserData(userId)

        // Load
        controller.getTodayClockIn(userId)
        controller.getTodayClockOut(userId)

        return view
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initViews(view: View){
        btnCheckInOut = view.findViewById(R.id.btnCheckInOut)
        timeCheckIn = view.findViewById(R.id.time_checkin)
        timeCheckOut = view.findViewById(R.id.time_checkout)
    }

    private fun setupButtonListeners() {
        btnCheckInOut.setOnClickListener {
            isCheckedIn = !isCheckedIn
            val now = LocalDateTime.now()
            val dateFormat = now.format(DateTimeUtils.displayDate)
            val timeFormat = now.format(DateTimeUtils.displayTime)

            if (isCheckedIn) {
                toggleBtnStatusCheckInOut()
                timeCheckIn.text = timeFormat
                addCheckInLog(requireContext(), dateFormat, timeFormat)

                controller.clockInUser(userId)  //เรียก API ไปยัง server เพื่อบันทึกเวลา //API จะเก็บเวลาลงฐานข้อมูล
            } else {
                toggleBtnStatusCheckInOut()
                timeCheckIn.text = timeFormat
                addCheckOutLog(requireContext(), dateFormat, timeFormat)

                controller.clockOutUser(userId)
            }
        }
    }

    private fun setTextTimeCheckIn(clockInTime: Long) {
        val formattedTime = DateTimeUtils.formatTimeFromMillis(clockInTime)
        timeCheckIn.text = formattedTime
    }

    private fun setTextTimeCheckOut(clockOutTime: Long){
        val formattedTime = DateTimeUtils.formatTimeFromMillis(clockOutTime)
        timeCheckOut.text = formattedTime
    }

    private fun toggleBtnStatusCheckInOut(){
           if (isCheckedIn){
               btnCheckInOut.text = "Check Out"
               btnCheckInOut.setBackgroundResource(R.drawable.bg_button_check_out)

            } else {
               btnCheckInOut.text = "Check In"
               btnCheckInOut.setBackgroundResource(R.drawable.bg_button_check_in)
            }
    }

    private fun addCheckInLog (context: Context, date:String, time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list
        log.add(ActivityLogManager.createLog(date, "Check-in", "Check-in: $time")) //add new log
        ActivityLogManager.putActivityLog(
            context,
            log
        ) //save log in sharedPreference as JSON format
    }

    private fun addCheckOutLog (context: Context, date:String, time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list
        log.add(ActivityLogManager.createLog(date, "Check-out", "Check-out: $time"))
        ActivityLogManager.putActivityLog(context, log)
    }

    override fun showLoading(isLoading: Boolean) {

    }

    override fun onError(message: String) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun onClockInSuccess(clockInTime: Long?) {
        if(clockInTime != null){
            setTextTimeCheckIn(clockInTime)
            val dateFormat = DateTimeUtils.formatTimeFromMillis(clockInTime)
            Toast.makeText(requireContext(), "Check-in success at $dateFormat", Toast.LENGTH_SHORT).show()
        }
        else {
            ToastUtils.showToast(requireContext(), R.string.check_in_error)
        }
    }

    override fun displayUserData(user: User?) {

    }

    override fun onLeaveApplicationSuccess(leaveRequest: LeaveData?) {

    }

    override fun onNoAttendanceData() {
        ToastUtils.showToast(requireContext(), R.string.no_attendance_data)
    }

    override fun onClockOutSuccess(clockOutTime: Long?) {
        if(clockOutTime != null){
            setTextTimeCheckOut(clockOutTime)
            val dateFormat = DateTimeUtils.formatTimeFromMillis(clockOutTime)
            Toast.makeText(requireContext(), "Check-out success at $dateFormat", Toast.LENGTH_SHORT).show()
        }
        else {
            ToastUtils.showToast(requireContext(), R.string.check_out_error)
        }
    }
}

