package com.example.miniproject.enums

import android.content.Context
import com.example.miniproject.R

enum class PeriodType {
    FULL_DAY,
    AM,
    PM,
    NONE;

    fun getDisplayName(context: Context): String {
        return when (this) {
            FULL_DAY -> context.getString(R.string.period_full)
            AM -> context.getString(R.string.period_am)
            PM -> context.getString(R.string.period_pm)
            NONE -> context.getString(R.string.period_none)
        }
    }
}