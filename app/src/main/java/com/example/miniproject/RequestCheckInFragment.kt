package com.example.miniproject

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class RequestCheckInFragment : Fragment() {
    private lateinit var btnSave : Button
    private lateinit var btnCancel :Button

    private lateinit var icCalendarCheckInDate : ImageView

    private lateinit var editTime : EditText
    private lateinit var editTextCheckInDate : EditText

    private val PREFS_NAME = "UserPrefs"

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_request_check_in, container, false)

        editTime = view.findViewById(R.id.edit_time)

        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        editTextCheckInDate = view.findViewById(R.id.editTextCheckInDate)
        icCalendarCheckInDate = view.findViewById(R.id.imageCheckInDate)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        parentFragmentManager.setFragmentResultListener("checkInDate", viewLifecycleOwner){ _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextCheckInDate.setText(selectedDate)
            checkFields()
        }

        icCalendarCheckInDate.setOnClickListener {
            val calendarDialog = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString("resultKey", "checkInDate")
                }
            }
            calendarDialog.show(parentFragmentManager,"checkInDateField")
        }

        editTime.addTextChangedListener {
            checkFields()
        }

        btnSave.setOnClickListener {

        val currentDate = LocalDateTime.now()
        val dateFormat = currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))

        var date = editTextCheckInDate.text.toString()
        var time = editTime.text.toString()

          val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
          val editor = sharedPref.edit()
          editor.putString("checkInDate", date)
          editor.putString("checkInTime", time)
          editor.putBoolean("isCheckedIn", true)

          editor.apply()
          editTime.text.clear()
          editTextCheckInDate.text.clear()

            Toast.makeText(requireContext(), getString(R.string.toast_data_applied), Toast.LENGTH_SHORT).show()
          addRequestCheckInLog(requireContext(), dateFormat, date, time)
        }

        btnCancel.setOnClickListener {
            editTime.text.clear()
            editTextCheckInDate.text.clear()
            Toast.makeText(requireContext(), getString(R.string.toast_data_deleted), Toast.LENGTH_SHORT).show()
        }

         return  view
    }


    fun isValidTimeFormat(time: String): Boolean{
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(time, formatter)  //Change time (from user) to Localtime
            true
        }
         catch (e: DateTimeException){
             false
         }
    }

    fun checkFields(){
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

    fun addRequestCheckInLog (context: Context, date:String, reqDate:String , time:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(ActivityLogManager.createLog(date, "Request Check-in","Request Check-in Date: $reqDate, Time: $time"))
        ActivityLogManager.putActivityLog(context, log)
    }

}

