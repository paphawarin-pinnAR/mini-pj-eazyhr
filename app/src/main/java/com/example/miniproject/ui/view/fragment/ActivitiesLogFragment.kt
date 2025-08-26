package com.example.miniproject.ui.view.fragment

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
import com.example.miniproject.enums.ActivityType
import com.example.miniproject.utils.DateTimeUtils

import java.time.LocalDate

class ActivitiesLogFragment : Fragment() {
    private lateinit var tableActivitiesLog: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activities_log, container, false)

        initViews(view)
        showActivitiesLog()

        return view
    }

    private fun initViews(view: View){
        tableActivitiesLog = view.findViewById(R.id.tableHistory)
    }

    private fun showActivitiesLog() {

        val sortedLogList = sortLogCellList()

        while (tableActivitiesLog.childCount > 1) {
            tableActivitiesLog.removeViewAt(1)
        }

        for ((index, log) in sortedLogList.withIndex()) {
            val row = createLogRow(index, log)
            tableActivitiesLog.addView(row)
        }
    }

    private fun sortLogCellList () : List<ActivityLogData> {
        val logs = ActivityLogManager.getActivityLog(requireContext())
        val sortedLogList = logs.sortedWith(
            compareBy<ActivityLogData> { it.priority } //sort by low->high priority 1,2,3,..,99
                .thenByDescending { DateTimeUtils.parseDateUser(it.date) }  //in case: same priority, sort by date
                .thenBy { ActivityType.fromString(it.type).getPriority() }//in case same priority and date, sort by type priority
        )
        return sortedLogList
    }

    private fun createLogCellTextView (text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 14f
            setPadding(16, 18, 16, 18)
            isSingleLine = false
            maxLines = 10
            ellipsize = null
            layoutParams = TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 1f
            )
        }
    }

    private fun createLogRow(index: Int, log: ActivityLogData) : TableRow {
        val row = TableRow(requireContext()) //create a new row by using the context of fragment to display the data in the table history

        val colorOdd = ContextCompat.getColor(requireContext(), R.color.medium_blue)
        val colorEven = ContextCompat.getColor(requireContext(), R.color.light_blue)
        val backgroundColor = if(index % 2 == 0) colorOdd else colorEven
        row.setBackgroundColor(backgroundColor)

        val dateView = createLogCellTextView(log.date)
        val typeView = createLogCellTextView(log.type)
        val detailView = createLogCellTextView(log.detail)

        row.addView(dateView)
        row.addView(typeView)
        row.addView(detailView)

        return row
    }
}





