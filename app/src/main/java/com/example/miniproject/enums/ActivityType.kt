package com.example.miniproject.enums

import android.content.Context
import com.example.miniproject.R

enum class ActivityType {
    CHECK_IN,
    REQUEST_CHECK_IN,
    CHECK_OUT,
    REQUEST_CHECK_OUT,
    REQUEST_OT,
    REQUEST_LEAVE,
    UNKNOWN;

    fun getDisplayName(context: Context): String {
        //this = Enum ที่เรียก getDisplayName()
        //when(this) เช็คว่า Enum ตัวนี้คือค่าไหน > คืนค่า string ตาม Enum นั้นๆ
        return when (this) {
            CHECK_IN -> context.getString(R.string.activity_check_in)
            REQUEST_CHECK_IN -> context.getString(R.string.activity_request_check_in)
            CHECK_OUT -> context.getString(R.string.activity_check_out)
            REQUEST_CHECK_OUT -> context.getString(R.string.activity_request_check_out)
            REQUEST_OT -> context.getString(R.string.activity_request_ot)
            REQUEST_LEAVE -> context.getString(R.string.activity_request_leave)
            UNKNOWN -> context.getString(R.string.activity_unknown)
        }
    }

    fun getPriority(): Int {
        return when (this) {
            CHECK_IN -> 1
            REQUEST_CHECK_IN -> 2
            CHECK_OUT -> 3
            REQUEST_CHECK_OUT -> 4
            REQUEST_OT -> 5
            REQUEST_LEAVE -> 6
            UNKNOWN -> 7
        }
    }

    companion object {
        fun fromString(type: String): ActivityType {
            return when (type.lowercase()) {
                "check-in" -> CHECK_IN
                "request check-in" -> REQUEST_CHECK_IN
                "check-out" -> CHECK_OUT
                "request check-out" -> REQUEST_CHECK_OUT
                "request ot" -> REQUEST_OT
                "request leave" -> REQUEST_LEAVE
                else -> UNKNOWN
            }
        }
    }
}
