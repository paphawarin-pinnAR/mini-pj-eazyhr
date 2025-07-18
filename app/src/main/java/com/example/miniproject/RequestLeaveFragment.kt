package com.example.miniproject

import android.content.Context
import android.icu.text.Transliterator.Position
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class RequestLeaveFragment : Fragment() {
    private lateinit var spinnerLeaveType: Spinner
    private lateinit var icCalendarFromDate: ImageView
    private lateinit var icCalendarToDate: ImageView
    private lateinit var editTextFromDate: EditText
    private lateinit var editTextToDate: EditText
    private lateinit var spinnerPeriodType: Spinner
    private lateinit var editReason: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val PREFS_NAME = "UserPrefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_leave, container, false)

        spinnerLeaveType = view.findViewById(R.id.spinner_leave_type)
        icCalendarFromDate = view.findViewById(R.id.imageLeaveFromDate)
        icCalendarToDate = view.findViewById(R.id.imageLeaveToDate)
        editTextFromDate = view.findViewById(R.id.editTextFromDate)
        editTextToDate = view.findViewById(R.id.editTextToDate)
        spinnerPeriodType = view.findViewById(R.id.spinner_leave_period)
        editReason = view.findViewById(R.id.editTextReason)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        parentFragmentManager.setFragmentResultListener(
            "fromDate",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextFromDate.setText(selectedDate)
            checkFields()
        }

        parentFragmentManager.setFragmentResultListener("toDate", viewLifecycleOwner) { _, bundle ->
            val selectedDate = bundle.getString("selectedDate")
            editTextToDate.setText(selectedDate)
            checkFields()
        }

        icCalendarFromDate.setOnClickListener {
            val calendarDialog = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        "resultKey",
                        "fromDate"
                    )   // Note that the Dialog is opened from the FromDate field
                }
            }

            calendarDialog.show(
                parentFragmentManager,
                "fromDateField"
            )  // "calendarDialog" is Tag name (Optional) using for find the dialog
        }

        icCalendarToDate.setOnClickListener {
            val calendarDialog = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        "resultKey",
                        "toDate"
                    )   // Note that the Dialog is opened from the FromDate field
                }
            }

            calendarDialog.show(
                parentFragmentManager,
                "toDateField"
            ) // "calendarDialog" is Tag name (Optional) using for find the dialog
        }

        editTextFromDate.addTextChangedListener {
            checkFields()
        }

        editTextToDate.addTextChangedListener {
            checkFields()
        }

        editReason.addTextChangedListener {
            checkFields()
        }

        //get type from string.xml
        val leaveTypeList = resources.getStringArray(R.array.leaveType).toList()
        val periodTypeList = resources.getStringArray(R.array.periodType).toList()

        setupSpinner(spinnerLeaveType, leaveTypeList) { typeSelected ->
            val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("leaveRequestType", typeSelected).apply()

            // LeaveType.values() คือการดึง enum ทุกตัวออกมาเป็น array เพื่อใช้ .find ในการวนเช็คไทป์
            // convert the value that is selected from user in the Spinner (String) to enum LeaveType
            // use .find to find enum ที่ displayType is match typeSelected
            val selectedType = LeaveType.values().find { it.displayType == typeSelected } ?: LeaveType.NONE

            when (selectedType) {
                LeaveType.ANNUAL-> {
                    Toast.makeText(requireContext(), getString(R.string.leave_annual), Toast.LENGTH_SHORT).show()
                }

               LeaveType.PRIVATE_LEAVE -> {
                    Toast.makeText(requireContext(),getString(R.string.leave_private), Toast.LENGTH_SHORT).show()
                }

                LeaveType.SICK-> {
                    Toast.makeText(requireContext(), getString(R.string.leave_sick), Toast.LENGTH_SHORT).show()
                }

               LeaveType.SPECIAL_HOLIDAY -> {
                    Toast.makeText(requireContext(), getString(R.string.leave_special_holiday), Toast.LENGTH_SHORT).show()
                }

                LeaveType.NONE -> {
                    Toast.makeText(requireContext(), getString(R.string.empty_leave_item), Toast.LENGTH_SHORT).show()
                }
            }
            checkFields()
        }

        setupSpinner(spinnerPeriodType, periodTypeList) { periodSelected ->
            val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("leaveRequestPeriod", periodSelected).apply()

            val selectedPeriod = PeriodType.values().find { it.displayPeriod == periodSelected } ?: PeriodType.NONE

            when (selectedPeriod) {
                PeriodType.AM -> {
                    Toast.makeText(requireContext(), getString(R.string.period_am), Toast.LENGTH_SHORT).show()
                }

                PeriodType.PM -> {
                    Toast.makeText(requireContext(),getString(R.string.period_pm), Toast.LENGTH_SHORT).show()
                }

                PeriodType.FULL_DAY -> {
                    Toast.makeText(requireContext(), getString(R.string.period_full_day), Toast.LENGTH_SHORT).show()
                }

                PeriodType.NONE -> {
                    Toast.makeText(requireContext(), getString(R.string.empty_period_item), Toast.LENGTH_SHORT).show()
                }
            }
            checkFields()
        }

        btnSave.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))

            var leaveType = spinnerLeaveType.selectedItem.toString()
            var fromDate = editTextFromDate.text.toString()
            var toDate = editTextToDate.text.toString()
            var leavePeriod = spinnerPeriodType.selectedItem.toString()
            var reason = editReason.text.toString()

            val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("leaveRequestType", leaveType) //save leaveRequestType selection into the SharedPref by using "leaveRequestType" key
            editor.putString("leaveDateFrom", fromDate)
            editor.putString("leaveDateTo", toDate)
            editor.putString("leaveRequestPeriod", leavePeriod)
            editor.putString("leaveReason", reason)

            editor.apply()
            spinnerLeaveType.setSelection(0)
            editTextFromDate.text.clear()
            editTextToDate.text.clear()
            spinnerPeriodType.setSelection(0)
            editReason.text.clear()
            Toast.makeText(requireContext(), getString(R.string.toast_data_applied), Toast.LENGTH_SHORT).show()

            addRequestLeaveLog(requireContext(), dateFormat, leaveType, fromDate, toDate, leavePeriod, reason)
        }

        btnCancel.setOnClickListener {
            spinnerLeaveType.setSelection(0)
            editTextFromDate.text.clear()
            editTextToDate.text.clear()
            spinnerPeriodType.setSelection(0)
            editReason.text.clear()
            Toast.makeText(requireContext(), getString(R.string.toast_data_deleted), Toast.LENGTH_SHORT).show()
        }

            return view
    }


     fun setupSpinner(spinner: Spinner, items: List<String>, onSelect: (String) -> Unit) {
        spinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                onSelect(items[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
     }


        fun checkFields() {
            val hasFromDate = editTextFromDate.text.isNotBlank()
            val hasToDate = editTextToDate.text.isNotBlank()
            val hasReason = editReason.text.isNotBlank()

            val hasLeaveType = spinnerLeaveType.selectedItemPosition != 0
            val hasPeriodType = spinnerPeriodType.selectedItemPosition != 0

            if (hasFromDate && hasToDate && hasReason && hasLeaveType && hasPeriodType) {
                btnSave.isEnabled = true
                btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            else {
                btnSave.isEnabled = false
                btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
            }
        }

    fun addRequestLeaveLog (context: Context, date:String, reqType: String, dateFrom:String, dateTo:String, period:String, reason:String) {
        val log = ActivityLogManager.getActivityLog(context).toMutableList() //.toMutableList() to allow add the new list

       // log.add(ActivityLogData(date, "Request Leave","Leave Type: $reqType, From Date: $dateFrom, To Date: $dateTo, Period: $period Reason: $reason"))
        log.add(ActivityLogManager.createLog(date, "Request Leave","Leave Type: $reqType, From Date: $dateFrom, To Date: $dateTo, Period: $period Reason: $reason"))
        ActivityLogManager.putActivityLog(context, log)
    }
    }








