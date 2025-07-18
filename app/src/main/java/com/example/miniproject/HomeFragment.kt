package com.example.miniproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.prefs.Preferences


class HomeFragment : Fragment() {
    lateinit var btnCheckInOut : Button
    lateinit var timeCheckIn : TextView
    lateinit var timeCheckOut :TextView

    var isCheckedIn: Boolean = false   //button state //false=check-out, true=check-in
    var timeCheckedIn: String? = null
    var timeCheckedOut: String? = null
    var date: String? = null

    private val PREFS_NAME = "UserPrefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        // 1st is a layout design that I made, 2nd is the container, which is the object of the view group class here
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        btnCheckInOut = view.findViewById(R.id.btnCheckInOut)
        timeCheckIn = view.findViewById(R.id.time_checkin)
        timeCheckOut = view.findViewById(R.id.time_checkout)


       btnCheckInOut.setOnClickListener {
           isCheckedIn = !isCheckedIn //toggle

           val currentDate = LocalDateTime.now()
           val dateFormat = currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
           val timeFormat = currentDate.format(DateTimeFormatter.ofPattern("HH:mm"))

            if (isCheckedIn){
                toggleBtnStatusCheckInOut()
                setTextDateCheckIn()
                setTextTimeCheckIn()

                addCheckInLog(requireContext(), dateFormat, timeFormat)
            }
            else {
                toggleBtnStatusCheckInOut()
                setTextDateCheckOut()
                setTextTimeCheckOut()

                addCheckOutLog(requireContext(), dateFormat, timeFormat)
            }

           saveData()
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onResume() {
        super.onResume()
        retrieveData()
    }

    fun setTextDateCheckIn() {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
        val currentDate = LocalDateTime.now()
        val currentDateCheckIn = currentDate.format(formatter)

        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkInDate", currentDateCheckIn)
        editor.apply()
    }

    fun setTextDateCheckOut() {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
        val currentDate = LocalDateTime.now()
        val currentDateCheckOut = currentDate.format(formatter)

        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkOutDate", currentDateCheckOut)
        editor.apply()
    }


    fun setTextTimeCheckIn(){
        val  currentTime = LocalTime.now()
        val  formatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTimeCheckIn = currentTime.format(formatter)

        timeCheckIn.text = currentTimeCheckIn

       val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
       val editor = sharedPref.edit()
        editor.putBoolean("isCheckedIn", true)
        editor.putString("checkInTime", currentTimeCheckIn)
        editor.apply()

    }

    fun setTextTimeCheckOut(){

        val  currentTime = LocalTime.now()
        val  formatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTimeCheckOut = currentTime.format(formatter)

        timeCheckOut.text = currentTimeCheckOut

        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isCheckedIn", false)
        editor.apply()
    }

    fun saveData(){
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        timeCheckedIn = timeCheckIn.text.toString()
        timeCheckedOut = timeCheckOut.text.toString()

        val editor = sharedPref.edit()
        editor.putString("checkInTime", timeCheckedIn)
        editor.putString("checkOutTime", timeCheckedOut)
        editor.putBoolean("isCheckedIn", isCheckedIn)

        editor.apply()
    }

    fun retrieveData(){
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        timeCheckedIn = sharedPref.getString("checkInTime", "xx:xx")
        timeCheckedOut = sharedPref.getString("checkOutTime", "xx:xx")
        isCheckedIn = sharedPref.getBoolean("isCheckedIn", isCheckedIn)
        toggleBtnStatusCheckInOut()

        timeCheckIn.setText(timeCheckedIn)
        timeCheckOut.setText(timeCheckedOut)
    }

    fun toggleBtnStatusCheckInOut(){
           if (isCheckedIn){
               btnCheckInOut.text = "Check Out"
               btnCheckInOut.setBackgroundResource(R.drawable.bg_button_check_out)

            } else {
               btnCheckInOut.text = "Check In"
               btnCheckInOut.setBackgroundResource(R.drawable.bg_button_check_in)
            }
    }

    fun addCheckInLog (context: Context, date:String, time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(ActivityLogManager.createLog(date, "Check-in","Check-in: $time")) //add new log
        ActivityLogManager.putActivityLog(context, log) //save log in sharedPreference as JSON format
    }

    fun addCheckOutLog (context: Context, date:String, time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(ActivityLogManager.createLog(date, "Check-out","Check-out: $time"))
        ActivityLogManager.putActivityLog(context, log)
    }
}

