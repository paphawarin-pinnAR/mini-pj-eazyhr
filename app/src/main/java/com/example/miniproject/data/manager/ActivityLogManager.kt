package com.example.miniproject.data.manager

import android.content.Context
import com.example.miniproject.constants.AppConstants
import com.example.miniproject.data.model.ActivityLogData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ActivityLogManager {

    fun putActivityLog(context: Context, logs: List<ActivityLogData>) {
        val sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson().toJson(logs) //Gson is a class from Google lib //convert Object (List<ActivityLogData>) to JSON String
        val editor = sharedPref.edit()
        editor.putString(AppConstants.PREFS_LOGS, gson)
        editor.apply()
    }

    fun getActivityLog(context: Context): ArrayList<ActivityLogData> {
        val sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val gson = sharedPref.getString(AppConstants.PREFS_LOGS, "[]")
        // Note that with Gson, the JSON represents a List of ActivityLogData objects
        val type = object : TypeToken<ArrayList<ActivityLogData>>() {}.type
        //List of ActivityLogData obtained from JSON conversion (ex.SharedPreferences, Database) // Convert JSON string to ArrayList of ActivityLogData
        val arrayList: ArrayList<ActivityLogData> = Gson().fromJson(gson, type)  //gson = string, type(expected result) = arraylist (object)

        return arrayList
    }

    fun typeOrder(type: String): Int {
        return when (type.lowercase()) {
            "check-in" -> 1
            "request check-in" -> 2
            "check-out" -> 3
            "request check-out" -> 4
            "request ot" -> 5
            "request leave" -> 6
            else-> 7
        }
    }

    fun createLog(date: String, type: String, detail: String): ActivityLogData {
        val priority = typeOrder(type)
        return ActivityLogData(date, type, detail, priority)
    }

}
