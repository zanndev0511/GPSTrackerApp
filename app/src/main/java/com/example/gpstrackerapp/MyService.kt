package com.example.gpstrackerapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.gpstrackerapp.App.Companion.CHANNEL_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyService: Service() {
    override fun onCreate() {
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var input_lat = intent!!.getStringExtra("inputExtra_lat")
        var input_lng = intent!!.getStringExtra("inputExtra_lng")

        var location_lat = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("lat").setValue(input_lat)
        var location_lng = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("lng").setValue(input_lng)
        var notificationIntent: Intent = Intent(this, UserLocationMainActivity::class.java)
        var pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        var notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Example Service")
            .setContentText("Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}