package com.example.miniproject.ui.fragment

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
import androidx.core.content.ContextCompat
import com.example.miniproject.data.model.ActivityLogData
import com.example.miniproject.data.manager.ActivityLogManager
import com.example.miniproject.R
import com.example.miniproject.utils.DateTimeUtils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivitiesLogFragment : Fragment() {
    private lateinit var tableActivitiesLog: TableLayout

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
        val displayDate = DateTimeUtils.displayDate
        val logs = ActivityLogManager.getActivityLog(requireContext())
        val sortedLogList = logs.sortedWith(
            compareBy<ActivityLogData> { it.priority } //sort by low->high priority 1,2,3,..,99
                .thenByDescending { LocalDate.parse(it.date, displayDate) }  //in case: same priority, sort by date
                .thenBy { ActivityLogManager.typeOrder(it.type) }//in case same priority and date, sort by type order
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
        val color = ContextCompat.getColor(requireContext(), R.color.light_blue)
        return TextView(requireContext()).apply {
           this.text = text
            textSize = 14f
            setPadding(16, 18, 16, 18)
            setBackgroundColor(color)
            setSingleLine(false)
            maxLines = 10
            ellipsize = null
            layoutParams = TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f
            )
        }
    }

}





