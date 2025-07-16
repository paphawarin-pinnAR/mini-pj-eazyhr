package com.example.miniproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.lifecycle.whenResumed

import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivitiesLogFragment : Fragment() {
    private lateinit var tableActivitiesLog: TableLayout

    private val PREFS_NAME = "UserPrefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activities_log, container, false)

        tableActivitiesLog = view.findViewById(R.id.tableHistory)
        showActivitiesLog()

        //showActivitiesLog(requireContext(), tableHistory)

        return view
    }

//    fun showActivitiesLog (context: Context, tableLayout: TableLayout) {
//        while (tableLayout.childCount > 1) {
//            tableLayout.removeViewAt(1)
//        }
//
//        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//
//        val checkInDate = sharedPref.getString("checkInDate", "--:--") ?: "dummy"
//        val checkInTime = sharedPref.getString("checkInTime", "--:--") ?: "dummy"
//
//         if (checkInTime != "xx:xx") {
//            addRow(tableLayout, checkInDate, "Check-in", "Check-in: $checkInTime")
//        }
//
//   }

//    fun addRow(tableLayout: TableLayout, date: String, type: String, detail: String) {
//        val context = tableLayout.context
//        val row = TableRow(context)  //create new table row using the context
//
//        val dateView = TextView(context).apply {
//            text = date
//            textSize = 14f
//        }
//
//        val typeView = TextView(context).apply {
//            text = type
//            textSize = 14f
//        }
//
//        val DetailView = TextView(context).apply {
//            text = detail
//            textSize = 14f
//        }
//
//        row.addView(dateView)
//        row.addView(typeView)
//        row.addView(DetailView)
//
//        tableLayout.addView(row)

//    }

    fun showActivitiesLog() {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val logs = ActivityLogManager.getActivityLog(requireContext())
        val sortedLogList = logs.sortedWith(
            compareBy<ActivityLogData> { it.priority } //sort by low->high priority 1,2,3,..,99
                .thenByDescending { LocalDate.parse(it.date, formatter) }  //in case: same priority, sort by date
                .thenBy {ActivityLogManager.typeOrder(it.type) }//in case same priority and date, sort by type order
        )

               while (tableActivitiesLog.childCount > 1) {
           tableActivitiesLog.removeViewAt(1)
        }

        for (log in sortedLogList) {
            val row = TableRow(requireContext()) //create a new row by using the context of fragment to display the data in the table history

            val dateView = TextView(requireContext()).apply {
                text = log.date
                textSize = 14f
                setPadding(16, 18, 16, 18)
                setSingleLine(false)
                maxLines = 5
                ellipsize = null
                layoutParams = TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f
                )
            }

            val typeView = TextView(requireContext()).apply {
                text = log.type
                textSize = 14f
                setPadding(16, 18, 16, 18)
                setSingleLine(false)
                maxLines = 8
                ellipsize = null
                layoutParams = TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f
                )
            }

            val detailView = TextView(requireContext()).apply {
                text = log.detail
                textSize = 14f
                setPadding(16, 18, 16, 18)
                setSingleLine(false)
                maxLines = 10
                ellipsize = null
                layoutParams = TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 1f
                )
            }

            row.addView(dateView)
            row.addView(typeView)
            row.addView(detailView)

            tableActivitiesLog.addView(row)
        }

    }


}





