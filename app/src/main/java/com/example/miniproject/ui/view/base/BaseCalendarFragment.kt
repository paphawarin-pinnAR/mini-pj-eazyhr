package com.example.miniproject.ui.view.base

import android.app.Activity
import android.content.Intent
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.ui.view.activity.SecondActivity

abstract class BaseCalendarFragment : Fragment() {

    private var targetEditText: EditText? = null
    private var lastSelectedDate: String? = null  //store the latest date selection

    //create only one launcher tied to the Fragment's lifecycle
    //registerForActivityResult helps open the Activity and wait for the result to be sent back
    private val calendarLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //callback lambda; It will be called immediately after the Activity opened with calendarLauncher.launch(intent) is closed
        if (result.resultCode == Activity.RESULT_OK) {
            //get the data sent back from the Activity via Intent
            lastSelectedDate = result.data?.getStringExtra(AppConstants.SELECTED_DATE_REQ)
            lastSelectedDate?.let {
               targetEditText?.setText(it)
               lastSelectedDate = it
            }
        }
    }

    //open CalendarActivity using the launcher
    fun openCalendar(editText: EditText) {
        targetEditText = editText
        val intent = Intent(requireContext(), SecondActivity::class.java)
        lastSelectedDate?.let { intent.putExtra(AppConstants.SELECTED_DATE_REQ, it) }
        calendarLauncher.launch(intent)
    }
}