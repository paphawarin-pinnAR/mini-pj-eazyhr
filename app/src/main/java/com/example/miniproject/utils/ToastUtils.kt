package com.example.miniproject.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtils {
     //Annotation: The messageResId should only be a string resource ID (e.g., R.string.some_text)
    fun showToast(context: Context, @StringRes messageResId: Int) {
        Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}