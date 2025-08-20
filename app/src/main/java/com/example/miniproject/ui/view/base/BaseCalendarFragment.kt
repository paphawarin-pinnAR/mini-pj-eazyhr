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

    //Create only one launcher tied to the Fragment's lifecycle
    //registerForActivityResult helps open the Activity and wait for the result to be sent back
    private val calendarLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //callback lambda; It will be called immediately after the Activity opened with calendarLauncher.launch(intent) is closed
        if (result.resultCode == Activity.RESULT_OK) {
            //Get the data sent back from the Activity via Intent
            val selectedDate = result.data?.getStringExtra(AppConstants.SELECTED_DATE_REQ)
            selectedDate?.let {
               targetEditText?.setText(it)
            }
        }
    }

    //Open CalendarActivity using the launcher
    fun openCalendar(editText: EditText) {
        targetEditText = editText
        val intent = Intent(requireContext(), SecondActivity::class.java)
        calendarLauncher.launch(intent)
    }
}