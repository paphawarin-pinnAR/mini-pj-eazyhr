package com.example.miniproject.ui.fragment

import android.content.Context
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
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.enums.LeaveType
import com.example.miniproject.enums.PeriodType
import com.example.miniproject.R
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.time.LocalDateTime
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_leave, container, false)

        // Initialize views
        initViews(view)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        // Set up fragment result listener
        setupFragmentResultListener()

        // Set up calendar icon listener
        setupCalendarIconLister()

        setupSpinners()

        setupTextChangeListeners()

        setupButtonListener()

        return view
    }

    private fun initViews(view: View) {
        spinnerLeaveType = view.findViewById(R.id.spinner_leave_type)
        icCalendarFromDate = view.findViewById(R.id.imageLeaveFromDate)
        icCalendarToDate = view.findViewById(R.id.imageLeaveToDate)
        editTextFromDate = view.findViewById(R.id.editTextFromDate)
        editTextToDate = view.findViewById(R.id.editTextToDate)
        spinnerPeriodType = view.findViewById(R.id.spinner_leave_period)
        editReason = view.findViewById(R.id.editTextReason)
        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btn_cancel)
    }

    private fun clearFields() {
        spinnerLeaveType.setSelection(0)
        editTextFromDate.text.clear()
        editTextToDate.text.clear()
        spinnerPeriodType.setSelection(0)
        editReason.text.clear()
    }

    private fun setupSpinners() {
        //get type from string.xml
        val leaveTypeList = resources.getStringArray(R.array.leaveType).toList()
        val periodTypeList = resources.getStringArray(R.array.periodType).toList()

        setupSpinnerItemSelectedListener(spinnerLeaveType, leaveTypeList) { typeSelected ->
            val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("leaveRequestType", typeSelected).apply()

            // LeaveType.values() คือการดึง enum ทุกตัวใน LeaveType ออกมาเป็น array เพื่อใช้ .find หาค่าที่ getDisplayName(...) ตรงกับ typeSelected
            // convert the value that is selected from user in the Spinner (String) to enum LeaveType
            // use .find to find enum ที่ displayType is match typeSelected
            val selectedType = LeaveType.values().find {
                it.getDisplayName(
                    context = requireContext()
                ) == typeSelected
            } ?: LeaveType.NONE

            // เอา enum ที่เลือก มาแสดงชื่ออีกครั้งด้วย Toast
            val displayToast = selectedType.getDisplayName(context = requireContext())
            Toast.makeText(requireContext(), displayToast, Toast.LENGTH_SHORT).show()

            checkFields()
        }

        setupSpinnerItemSelectedListener(spinnerPeriodType, periodTypeList) { periodSelected ->
            val sharedPref = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("leaveRequestPeriod", periodSelected).apply()

            val selectedPeriod = PeriodType.values().find {
                it.getDisplayName(context = requireContext()) == periodSelected
            } ?: PeriodType.NONE

            val displayToast = selectedPeriod.getDisplayName(context = requireContext())
            Toast.makeText(requireContext(), displayToast, Toast.LENGTH_SHORT).show()

            checkFields()
        }
    }

    private fun setupFragmentResultListener(){
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
    }

    private fun setupCalendarIconLister(){
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
    }

    private fun setupTextChangeListeners() {
        editTextFromDate.addTextChangedListener {
            checkFields()
        }

        editTextToDate.addTextChangedListener {
            checkFields()
        }

        editReason.addTextChangedListener {
            checkFields()
        }
    }

    private fun setupSpinnerItemSelectedListener(
        spinner: Spinner,
        items: List<String>,
        onSelect: (String) -> Unit
    ) {
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

    private fun setupButtonListener() {
        btnSave.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val dateFormat = currentDate.format(DateTimeUtils.displayDate)

            var leaveType = spinnerLeaveType.selectedItem.toString()
            var fromDate = editTextFromDate.text.toString()
            var toDate = editTextToDate.text.toString()
            var leavePeriod = spinnerPeriodType.selectedItem.toString()
            var reason = editReason.text.toString()

            val sharedPref = requireActivity().getSharedPreferences(
                AppConstants.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(
                "leaveRequestType",
                leaveType
            ) //save leaveRequestType selection into the SharedPref by using "leaveRequestType" key
            editor.putString("leaveDateFrom", fromDate)
            editor.putString("leaveDateTo", toDate)
            editor.putString("leaveRequestPeriod", leavePeriod)
            editor.putString("leaveReason", reason)

            editor.apply()
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_applied)

            addRequestLeaveLog(
                requireContext(),
                dateFormat,
                leaveType,
                fromDate,
                toDate,
                leavePeriod,
                reason
            )
        }

        btnCancel.setOnClickListener {
            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_deleted)
        }

    }

    private fun checkFields() {
        val hasFromDate = editTextFromDate.text.isNotBlank()
        val hasToDate = editTextToDate.text.isNotBlank()
        val hasReason = editReason.text.isNotBlank()

        val hasLeaveType = spinnerLeaveType.selectedItemPosition != 0
        val hasPeriodType = spinnerPeriodType.selectedItemPosition != 0

        if (hasFromDate && hasToDate && hasReason && hasLeaveType && hasPeriodType) {
            btnSave.isEnabled = true
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            btnSave.isEnabled = false
            btnSave.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }
    }

    private fun addRequestLeaveLog(
        context: Context,
        date: String,
        reqType: String,
        dateFrom: String,
        dateTo: String,
        period: String,
        reason: String
    ) {
        val log = ActivityLogManager.getActivityLog(context)
            .toMutableList() //.toMutableList() to allow add the new list

        log.add(
            ActivityLogManager.createLog(
                date,
                "Request Leave",
                "Leave Type: $reqType, From Date: $dateFrom, To Date: $dateTo, Period: $period Reason: $reason"
            )
        )
        ActivityLogManager.putActivityLog(context, log)
    }
}








