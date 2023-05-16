package com.example.gpstrackerapp

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MyLocationService : BroadcastReceiver() {
    companion object {
        var ACTION_PROCESS_UPDATE: String = "com.example.gpstrackerapp.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!= null){
            var action = intent.action
            if (ACTION_PROCESS_UPDATE.equals(action)){
                var result: LocationResult? = LocationResult.extractResult(intent)
                if (result!= null){
                    var location: Location = result.lastLocation!!
                    try {
                        UserLocationMainActivity.instance!!.update_location(location.latitude, location.longitude)
//                        Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()
                    }catch (e: java.lang.Exception){
//                        If app in kill mode
                        Toast.makeText(context, "Running", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}