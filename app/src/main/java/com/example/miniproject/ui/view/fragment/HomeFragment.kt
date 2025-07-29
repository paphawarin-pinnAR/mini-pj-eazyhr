package com.example.miniproject.ui.view.fragment

import HrController
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
import com.example.miniproject.data.model.User
import com.example.miniproject.ui.view.HrView
import com.example.miniproject.utils.DateTimeUtils
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

        // สร้าง controller โดยส่ง reference ของ activity นี้ (view) ไปด้วย // this คือ HomeFragment ที่ implement HrView
        controller = HrController(this)

        // Initialize views
        initViews(view)

        setupButtonListeners()

        // โหลดข้อมูล user จาก API
        controller.loadUserData(userId)

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

                controller.clockInUser(userId)  // เรียก API เช็คอิน
            } else {

                toggleBtnStatusCheckInOut()
            }
        }
    }


    private fun setTextDateCheckIn() {
        val currentDate = LocalDateTime.now()
        val currentDateCheckIn = currentDate.format(DateTimeUtils.displayDate)

        val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkInDate", currentDateCheckIn)
        editor.apply()
    }

    private fun setTextDateCheckOut() {
        val currentDate = LocalDateTime.now()
        val currentDateCheckOut = currentDate.format(DateTimeUtils.displayDate)

        val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkOutDate", currentDateCheckOut)
        editor.apply()
    }


    private fun setTextTimeCheckIn(time: String){
        val currentTime = LocalTime.now()
        val currentTimeCheckIn = currentTime.format(DateTimeUtils.displayTime)

        timeCheckIn.text = currentTimeCheckIn

    }

    private fun setTextTimeCheckOut(){
        val  currentTime = LocalTime.now()
        val currentTimeCheckOut = currentTime.format(DateTimeUtils.displayTime)

        timeCheckOut.text = currentTimeCheckOut

        val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isCheckedIn", false)
        editor.apply()
    }

    private fun saveData(){
        val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

        timeCheckedIn = timeCheckIn.text.toString()
        timeCheckedOut = timeCheckOut.text.toString()

        val editor = sharedPref.edit()
        editor.putString("checkInTime", timeCheckedIn)
        editor.putString("checkOutTime", timeCheckedOut)
        editor.putBoolean("isCheckedIn", isCheckedIn)

        editor.apply()
    }

    private fun retrieveData(){
        val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        timeCheckedIn = sharedPref.getString("checkInTime", "xx:xx")
        timeCheckedOut = sharedPref.getString("checkOutTime", "xx:xx")
        isCheckedIn = sharedPref.getBoolean("isCheckedIn", isCheckedIn)
        toggleBtnStatusCheckInOut()

        timeCheckIn.setText(timeCheckedIn)
        timeCheckOut.setText(timeCheckedOut)
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
        Toast.makeText(requireContext(), "Check-in success at $clockInTime", Toast.LENGTH_SHORT).show()
    }

    override fun displayUserData(user: User?) {

    }
}

