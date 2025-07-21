package com.example.miniproject.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.miniproject.R

enum class LeaveType(@StringRes val resourceId: Int) {
    ANNUAL(R.string.leave_annual),
    PRIVATE(R.string.leave_private),
    SICK(R.string.leave_sick),
    SPECIAL_HOLIDAY(R.string.leave_special_holiday),
    NONE(R.string.leave_none);

    fun getDisplayName(context: Context): String {
        return context.getString(resourceId)
    }
}
