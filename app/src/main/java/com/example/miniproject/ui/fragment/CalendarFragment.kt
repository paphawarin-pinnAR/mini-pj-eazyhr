package com.example.miniproject.ui.fragment

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import com.example.miniproject.R
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarFragment : DialogFragment() {

    lateinit var  calendarView : CalendarView
    private var resultKey : String  = "defaultKey"  //default callback key

    override fun onCreate(savedInstanceState: Bundle?) {   //onCreate() โหลด argument ที่ส่งมา
        super.onCreate(savedInstanceState)

        resultKey = arguments?.getString("resultKey") ?: resultKey
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // create calendar object
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val formatter  = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDateResult = formatter.format(calendar.time)

            //Send the date back to the fragment that called it.
            val bundle = Bundle().apply {
                putString("selectedDate", formattedDateResult)
            }

            parentFragmentManager.setFragmentResult(resultKey, bundle)
            dismiss()

            }

        return view
    }

}