package com.example.miniproject.ui.fragment

import android.content.Context
import android.os.Bundle
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
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

        editTextCheckOutDate = view.findViewById(R.id.editTextCheckOutDate)
        icCalendarCheckOutDate = view.findViewById(R.id.imageCheckOutDate)
        editTime = view.findViewById(R.id.edit_time)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        parentFragmentManager.setFragmentResultListener("checkOutDate", viewLifecycleOwner){ _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextCheckOutDate.setText(selectedDate)
            checkFields()
        }

        icCalendarCheckOutDate.setOnClickListener {
            val calendarDialog = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString("resultKey", "checkOutDate")
                }
            }
            calendarDialog.show(parentFragmentManager,"checkOutDateField")
        }

        editTime.addTextChangedListener {
            checkFields()
        }


        btnSave.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))

            var date = editTextCheckOutDate.text.toString()
            var time = editTime.text.toString()

            val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("checkOutDate", date)
            editor.putString("checkOutTime", time)
            editor.putBoolean("isCheckedIn", false)

            editor.apply()
            editTime.text.clear()
            editTextCheckOutDate.text.clear()
            addRequestCheckOutLog(requireContext(), dateFormat, date, time)

            Toast.makeText(requireContext(), getString(R.string.toast_data_applied), Toast.LENGTH_SHORT).show()
        }

           btnCancel.setOnClickListener {
               editTime.text.clear()
               editTextCheckOutDate.text.clear()
               Toast.makeText(requireContext(), getString(R.string.toast_data_deleted), Toast.LENGTH_SHORT).show()
        }

        return view
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

    fun addRequestCheckOutLog (context: Context, date:String, reqDate:String, time:String) {
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


}