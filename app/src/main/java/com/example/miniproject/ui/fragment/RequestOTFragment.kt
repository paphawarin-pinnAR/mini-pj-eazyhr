package com.example.miniproject.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class RequestOTFragment : Fragment() {
    private lateinit var editTextOTDate : EditText
    private lateinit var icCalendarOTDate : ImageView
    private lateinit var checkboxBreakTime : CheckBox
    private lateinit var editTextFromTime : EditText
    private lateinit var editTextToTime : EditText
    private lateinit var editReason: EditText
    private lateinit var btnSave : Button
    private lateinit var btnCancel : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_ot, container, false)

        // Initialize views
        initViews(view)

        // Set default save button state
        btnSave.isEnabled = false

        // Set up fragment result listener
        setupFragmentResultListener()

        // Set up calendar icon listener
        setupCalendarIconLister()

        //Load and set default times
        val (plusDefaultTimeFrom, checkOutTime) = loadDefaultTimes()

        // Set up text change listeners for validation
        setupTextChangeListeners()

        setupBreakTimeCheckBox(plusDefaultTimeFrom, checkOutTime)

        // Set up button listeners
        setupButtonListeners()

        return view
    }

    private fun initViews(view: View){
        editTextOTDate = view.findViewById(R.id.editTextOTDate)
        icCalendarOTDate = view.findViewById(R.id.imageOTDate)
        checkboxBreakTime = view.findViewById(R.id.checkboxBreakTime)
        editTextFromTime = view.findViewById(R.id.edit_from_time)
        editTextToTime = view.findViewById(R.id.edit_to_time)
        editReason = view.findViewById(R.id.editTextReason)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("OTDate", viewLifecycleOwner) { _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextOTDate.setText(selectedDate)
            checkFields()
        }
    }

    private fun setupCalendarIconLister(){
        icCalendarOTDate.setOnClickListener {
            val calendarDialog = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString("resultKey", "OTDate")
                }
            }
            calendarDialog.show(parentFragmentManager, "OTDateField")
        }
    }

    private fun loadDefaultTimes(): Pair<String, String>{
        //get the check-in/out time data from sharedPref to set the new default From time & To time
        val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val timeFormat = DateTimeUtils.displayTime

        val checkInTime = sharedPref.getString("checkInTime", " ") ?: " "
        val checkOutTime = sharedPref.getString("checkOutTime", " ") ?: " "

        val defaultTimeFrom = LocalTime.parse(checkInTime, timeFormat)
        val plusDefaultTimeFrom = defaultTimeFrom.plusHours(9).format(timeFormat)

        editTextFromTime.setText(plusDefaultTimeFrom)
        editTextToTime.setText(checkOutTime)

        return Pair(plusDefaultTimeFrom, checkOutTime)
    }

     private fun setupBreakTimeCheckBox(plusDefaultTimeFrom: String, checkOutTime: String){
        //format time expected: convert String->LocalTime
         val timeFormat = DateTimeUtils.displayTime

         checkboxBreakTime.setOnCheckedChangeListener { _, isChecked: Boolean ->  //_ is buttonView: CompoundButton

             //Convert time String ('xx:xx') to LocalTime in 'HH:mm' format
             //(to add 20 mins then convert it to String again and display on the screen)
             val timeFrom = LocalTime.parse(plusDefaultTimeFrom, timeFormat)
             val timeTo = LocalTime.parse(checkOutTime, timeFormat)

             if (isChecked) {
                 val plusNewTimeFrom = timeFrom.plusMinutes(20).format(timeFormat)
                 val plusNewTimeTo = timeTo.plusMinutes(20).format(timeFormat)

                 editTextFromTime.setText(plusNewTimeFrom)
                 editTextToTime.setText(plusNewTimeTo)
             }
             else {
                 editTextFromTime.setText(plusDefaultTimeFrom)
                 editTextToTime.setText(checkOutTime)

             }
         }
     }

    fun isValidTimeFormat(time: String): Boolean{
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
        val hasDate = editTextOTDate.text.isNotBlank()
        val hasReason = editReason.text.isNotBlank()
        val TimeFromTextInput = editTextFromTime.text.toString()
        val TimeToTextInput = editTextToTime.text.toString()
        val hasValidTimeFrom = isValidTimeFormat(TimeFromTextInput)
        val hasValidTimeTo = isValidTimeFormat(TimeToTextInput)

        if(hasDate && hasValidTimeFrom && hasValidTimeTo && hasReason) {
            btnSave.isEnabled = true
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
        else {
            btnSave.isEnabled = false
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }
    }

    private fun clearFields() {
        editTextOTDate.text.clear()
        checkboxBreakTime.isChecked = false
        editTextFromTime.text.clear()
        editTextToTime.text.clear()
        editReason.text.clear()
    }

    private fun setupTextChangeListeners() {
        editTextFromTime.addTextChangedListener {
            checkFields()
        }
        editTextToTime.addTextChangedListener {
            checkFields()
        }
        editReason.addTextChangedListener {
            checkFields()
        }
    }


    private fun saveToPreferences(date: String,fromTime:String,toTime: String,reason: String, isBreakTimeChecked: Boolean){
        val sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("OTDate", date)
        editor.putString("OTTimeFrom", fromTime)
        editor.putString("OTTimeTo", toTime)
        editor.putString("OTReason", reason)
        editor.putBoolean("IsBreakTimeChecked", isBreakTimeChecked)

        editor.apply()
    }

    private fun setupButtonListeners() {
        //Save button
        btnSave.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeUtils.displayDate)

            val date = editTextOTDate.text.toString()
            val fromTime = editTextFromTime.text.toString()
            val toTime = editTextToTime.text.toString()
            val reason = editReason.text.toString()
            val isBreakTimeChecked = checkboxBreakTime.isChecked

            saveToPreferences(date, fromTime, toTime, reason, isBreakTimeChecked)
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_applied)

            addRequestOTLog(requireContext(), dateFormat, date, fromTime, toTime, reason)
        }

        //Cancel button
        btnCancel.setOnClickListener {
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_deleted)
        }

    }
}

    private fun addRequestOTLog (context: Context, date:String, reqDate:String, timeFrom:String, timeTo:String, reason:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

        log.add(
            ActivityLogManager.createLog(
                date,
                "Request OT",
                "OT Date: $reqDate, From Time: $timeFrom, To Time: $timeTo, Reason: $reason"
            )
        )
        ActivityLogManager.putActivityLog(context, log)
    }



