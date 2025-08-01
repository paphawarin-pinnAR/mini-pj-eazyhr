package com.example.miniproject.ui.view.fragment

import HrController
import HrRepository
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
import com.example.miniproject.data.model.LeaveData
import com.example.miniproject.data.model.LeaveRequest
import com.example.miniproject.data.model.User
import com.example.miniproject.data.network.ApiClient
import com.example.miniproject.ui.view.HrView
import com.example.miniproject.utils.DateTimeUtils
import com.example.miniproject.utils.ToastUtils
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class RequestLeaveFragment : Fragment() , HrView {
    private lateinit var spinnerLeaveType: Spinner
    private lateinit var icCalendarFromDate: ImageView
    private lateinit var icCalendarToDate: ImageView
    private lateinit var editTextFromDate: EditText
    private lateinit var editTextToDate: EditText
    private lateinit var spinnerPeriodType: Spinner
    private lateinit var editReason: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var controller: HrController
    private var userId: Long = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_leave, container, false)

        val repository = HrRepository(ApiClient.apiService)

        controller = HrController(this, repository)

        // Initialize views
        initViews(view)

        //set disable default for the "SAVE" button
        btnSave.isEnabled = false

        // โหลดข้อมูล user จาก API
        controller.loadUserData(userId)

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

    // เก็บค่าที่เลือกจาก Spinner ไว้ใช้ตอน save
    private var selectedLeaveType: LeaveType = LeaveType.NONE
    private var selectedPeriodType: PeriodType = PeriodType.NONE

    private fun setupSpinners() {
        val leaveTypeList = resources.getStringArray(R.array.leaveType).toList()
        val periodTypeList = resources.getStringArray(R.array.periodType).toList()

        setupSpinnerItemSelectedListener(spinnerLeaveType, leaveTypeList) { typeSelected ->
            selectedLeaveType = LeaveType.values().find {
                it.getDisplayName(requireContext()) == typeSelected
            } ?: LeaveType.NONE

            checkFields()
        }

        setupSpinnerItemSelectedListener(spinnerPeriodType, periodTypeList) { periodSelected ->
            selectedPeriodType = PeriodType.values().find {
                it.getDisplayName(requireContext()) == periodSelected
            } ?: PeriodType.NONE

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
            if (selectedLeaveType == LeaveType.NONE || selectedPeriodType == PeriodType.NONE) {
               //ToastUtils.showToast(requireContext(), "Please select valid leave type and period.")
                return@setOnClickListener
            }

            val fromDate = editTextFromDate.text.toString()
            val toDate = editTextToDate.text.toString()
            val reason = editReason.text.toString()

            val startDateMillis = convertDateStringToMillis(fromDate)
            val endDateMillis = convertDateStringToMillis(toDate)
            val totalDays = ((endDateMillis - startDateMillis) / (1000 * 60 * 60 * 24) + 1).toDouble()

            val leaveRequest = LeaveRequest(
                userId = userId,
                leaveCategory = selectedLeaveType.name,
                leavePeriod = selectedPeriodType.name,
                startDate = startDateMillis,
                endDate = endDateMillis,
                reason = reason,
                totalDays = totalDays
            )

            controller.applyForLeave(leaveRequest)

            clearFields()
            ToastUtils.showToast(requireContext(), R.string.data_applied)

            val dateFormat = LocalDateTime.now().format(DateTimeUtils.displayDate)
            addRequestLeaveLog(
                requireContext(),
                dateFormat,
                selectedLeaveType.getDisplayName(requireContext()),
                fromDate,
                toDate,
                selectedPeriodType.getDisplayName(requireContext()),
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

    private fun convertDateStringToMillis(dateString: String): Long {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())  // ปรับ format ให้ตรงกับที่ใช้จริง
        val date = format.parse(dateString)
        return date?.time ?: 0L
    }

    override fun showLoading(isLoading: Boolean) {

    }

    override fun onError(message: String) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun onClockInSuccess(clockInTime: Long?) {

    }

    override fun onClockOutSuccess(clockOutTime: Long?) {

    }

    override fun onNoAttendanceData() {

    }

    override fun displayUserData(user: User?) {

    }

    override fun onLeaveApplicationSuccess(leaveRequest: LeaveData?) {
        Toast.makeText(requireContext(), "ยื่นใบลาสำเร็จ รหัสคำขอ: ${leaveRequest?.id}", Toast.LENGTH_SHORT).show()
    }
}








