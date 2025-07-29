package com.example.miniproject.ui.view
import com.example.miniproject.data.model.User

// Activity/Fragment จะ implement interface นี้
interface HrView {
    fun showLoading(isLoading: Boolean)
    fun onError(message: String)
    fun onClockInSuccess(clockInTime: Long?)
    fun displayUserData(user: User?)
}