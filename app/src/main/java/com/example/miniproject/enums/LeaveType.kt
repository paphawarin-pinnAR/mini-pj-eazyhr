package com.example.miniproject.enums

import android.content.Context
import com.example.miniproject.R

enum class LeaveType {
    ANNUAL,
    PRIVATE,
    SICK,
    SPECIAL_HOLIDAY,
    NONE;

    fun getDisplayName(context: Context): String {
        return when (this) {
            ANNUAL -> context.getString(R.string.leave_annual)
            PRIVATE -> context.getString(R.string.leave_private)
            SICK -> context.getString(R.string.leave_sick)
            SPECIAL_HOLIDAY -> context.getString(R.string.leave_special_holiday)
            NONE -> context.getString(R.string.leave_none)
        }
    }
}