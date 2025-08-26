package com.example.miniproject.enums

import androidx.fragment.app.Fragment
import com.example.miniproject.ui.view.fragment.RequestCheckInFragment
import com.example.miniproject.ui.view.fragment.RequestCheckOutFragment
import com.example.miniproject.ui.view.fragment.RequestLeaveFragment
import com.example.miniproject.ui.view.fragment.RequestOTFragment

enum class RequestType(val fragmentClass: () -> Fragment) {
    REQUEST_CHECK_IN({ RequestCheckInFragment() }),
    REQUEST_CHECK_OUT({ RequestCheckOutFragment() }),
    REQUEST_OT({ RequestOTFragment() }),
    REQUEST_LEAVE({ RequestLeaveFragment() });

    companion object {
        fun fromPosition(position: Int): RequestType? {
            return values().getOrNull(position - 1) //position 0 = " "
        }
    }
}