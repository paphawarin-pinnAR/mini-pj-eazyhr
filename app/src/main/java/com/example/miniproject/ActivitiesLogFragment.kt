package com.example.miniproject

import android.content.Context
import android.graphics.Color
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

        return view
    }

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

            val dateView = createLogCellTextView(log.date)
            val typeView = createLogCellTextView(log.type)
            val detailView = createLogCellTextView(log.detail)

            row.addView(dateView)
            row.addView(typeView)
            row.addView(detailView)

            tableActivitiesLog.addView(row)
        }

    }

    private fun createLogCellTextView (text: String): TextView {
        return TextView(requireContext()).apply {
           this.text = text
            textSize = 14f
            setPadding(16, 18, 16, 18)
            setBackgroundColor(Color.parseColor("#E0F2FF"))
            setSingleLine(false)
            maxLines = 10
            ellipsize = null
            layoutParams = TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f
            )
        }
    }

}





