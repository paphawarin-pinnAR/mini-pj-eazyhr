package com.example.miniproject.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.miniproject.R

enum class PeriodType(@StringRes val resourceId: Int) {
    AM(R.string.period_am),
    PM(R.string.period_pm),
    FULL(R.string.period_full),
    NONE(R.string.period_none);

    fun getDisplayName(context: Context): String {
        return context.getString(resourceId)
    }
}