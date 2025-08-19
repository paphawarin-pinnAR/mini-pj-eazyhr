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
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime

class RequestCheckOutFragment : Fragment() {
    private lateinit var editTextCheckOutDate : EditText
    private lateinit var icCalendarCheckOutDate : ImageView
    private lateinit var editTime : EditText
    private lateinit var btnSave : Button
    private lateinit var btnCancel : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_check_out, container, false)

        // Initialize views
        initViews(view)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        // Set up fragment result listener
        setupFragmentResultListener()

        // Set up calendar icon listener
        setupCalendarIconListener()

        // Set up text change listeners for validation
        setupTextChangeListeners()

        // Set up button listeners
        setupButtonListeners()

        return view
    }

    private fun initViews(view: View){
        editTextCheckOutDate = view.findViewById(R.id.editTextCheckOutDate)
        icCalendarCheckOutDate = view.findViewById(R.id.imageCheckOutDate)
        editTime = view.findViewById(R.id.edit_time)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)
    }

    private fun setupButtonListeners(){
        //Save button
        btnSave.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeUtils.displayDate)

            val date = editTextCheckOutDate.text.toString()
            val time = editTime.text.toString()

            saveToPreference(date,time,false)
            clearFields()
            addRequestCheckOutLog(requireContext(), dateFormat, date, time)

            ToastUtils.showToast(requireContext(),R.string.data_applied)
        }
        //Cancel button
        btnCancel.setOnClickListener {
            clearFields()
            ToastUtils.showToast(requireContext(),R.string.data_deleted)
        }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("checkOutDate", viewLifecycleOwner){ _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextCheckOutDate.setText(selectedDate)
            checkFields()
        }
    }

    private fun setupCalendarIconListener(){
        icCalendarCheckOutDate.setOnClickListener {
            context?.let {
                val intent = Intent(it, SecondActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupTextChangeListeners() {
        editTime.addTextChangedListener {
            checkFields()
        }
    }

    private fun saveToPreference(date: String, time: String, isCheckedIn: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("checkOutDate", date)
        editor.putString("checkOutTime", time)
        editor.putBoolean("isCheckedIn", isCheckedIn)
        editor.apply()
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
        val hasDate = editTextCheckOutDate.text.isNotBlank()
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

    private fun addRequestCheckOutLog (context: Context, date:String, reqDate:String, time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(
            ActivityLogManager.createLog(
                date,
                "Request Check-out",
                "Request Check-out Date: $reqDate, Time: $time"
            )
        )
        ActivityLogManager.putActivityLog(context, log)
    }

    private fun clearFields() {
        editTime.text.clear()
        editTextCheckOutDate.text.clear()
    }


}