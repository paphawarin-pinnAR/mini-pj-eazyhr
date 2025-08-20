package com.example.miniproject.ui.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.ui.view.activity.SecondActivity
import com.example.miniproject.ui.view.base.BaseCalendarFragment
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime

class RequestCheckInFragment : BaseCalendarFragment() {
    private lateinit var btnSave : Button
    private lateinit var btnCancel :Button

    private lateinit var icCalendarCheckInDate : ImageView

    private lateinit var editTime : EditText
    private lateinit var editTextCheckInDate : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_check_in, container, false)

        // Initialize views
        initViews(view)

        // Set default save button state
        btnSave.isEnabled = false

        // Set up fragment result listener
        setupFragmentResultListener()

        // Set up calendar icon listener
        setupCalendarIconListener()

        // Set up text change listeners for validation
        setupTextChangeListeners()

        // Set up button listeners
        setupButtonListeners()

         return  view
    }

    private fun initViews(view: View){
        editTime = view.findViewById(R.id.edit_time)

        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        editTextCheckInDate = view.findViewById(R.id.editTextCheckInDate)
        icCalendarCheckInDate = view.findViewById(R.id.imageCheckInDate)
    }

    private fun setupFragmentResultListener(){
        parentFragmentManager.setFragmentResultListener("checkInDate", viewLifecycleOwner){ _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextCheckInDate.setText(selectedDate)
            checkFields()
        }
    }

    private fun setupCalendarIconListener(){
        icCalendarCheckInDate.setOnClickListener {
                openCalendar(editTextCheckInDate)
        }
    }

    private fun setupTextChangeListeners() {
        editTime.addTextChangedListener {
            checkFields()
        }
    }

    private fun saveToPreference(date: String, time: String, isCheckedIn:Boolean) {
        val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkInDate", date)
        editor.putString("checkInTime", time)
        editor.putBoolean("isCheckedIn", isCheckedIn)

        editor.apply()
    }

    private fun setupButtonListeners() {
        //Save button
        btnSave.setOnClickListener {

            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeUtils.displayDate)

            val date = editTextCheckInDate.text.toString()
            val time = editTime.text.toString()

            saveToPreference(date,time, true)
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_applied)
            addRequestCheckInLog(requireContext(), dateFormat, date, time)
        }

        //Cancel button
        btnCancel.setOnClickListener {
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_deleted)
        }

    }

    private fun isValidTimeFormat(time: String): Boolean{
        return try {
            val timeFormat = DateTimeUtils.displayTime
            LocalTime.parse(time, timeFormat)  //Change time (from user) to Localtime
            true
        }
         catch (e: DateTimeException){
             false
         }
    }

    private fun checkFields(){
        val hasDate = editTextCheckInDate.text.isNotBlank()
        val timeTextInput= editTime.text.toString()
        val hasValidTime = isValidTimeFormat(timeTextInput)

        if(hasDate && hasValidTime) {
            btnSave.isEnabled = true
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
        else {
            btnSave.isEnabled = false
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }
    }

    private fun addRequestCheckInLog (context: Context, date:String, reqDate:String , time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(
            ActivityLogManager.createLog(
                date,
                "Request Check-in",
                "Request Check-in Date: $reqDate, Time: $time"
            )
        )
        ActivityLogManager.putActivityLog(context, log)
    }

    private fun clearFields() {
        editTime.text.clear()
        editTextCheckInDate.text.clear()
    }

}

