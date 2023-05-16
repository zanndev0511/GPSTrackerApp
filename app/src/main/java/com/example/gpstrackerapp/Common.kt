package com.example.gpstrackerapp

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import java.text.DateFormat
import java.util.Date
import java.util.prefs.PreferenceChangeEvent
import java.util.zip.DataFormatException

object Common {
    val KEY_REQUEST_LOCATION_UPDATE = "requesting_location_update"

    fun getLocation(location: Location?): String {
        return if (location == null)
            "Unknown Location"
        else
            location.toString()
    }

    fun getLocationTitle(context: Context): String {
        return String.format("Location updated: ${DateFormat.getDateInstance().format(Date())}")
    }

    fun setRequestingLocationUpdates(context: Context, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUEST_LOCATION_UPDATE, value)
            .apply()
    }

    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUEST_LOCATION_UPDATE, false)
    }
}