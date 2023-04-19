package com.example.gpstrackerapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gpstrackerapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

    //    lateinit var binding: ActivityMainBinding
//    private lateinit var navView: NavigationView
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var actionBarToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
//    auth.signOut()
        if ((user == null) || (user!!.isEmailVerified == false)) {
            setContentView(R.layout.activity_main)
            getPermission()
        } else {
            var myIntent: Intent = Intent(this, UserLocationMainActivity::class.java)
            startActivity(myIntent)
            finish()
        }
    }

    public fun goToLogin(v: View) {
        var myIntent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(myIntent)
    }

    public fun goToRegister(v: View) {
        var myIntent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(myIntent)
    }

    public fun getPermission() {
        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("R.string.storage_permission_rationale_title")
                        .setMessage("R.string.storage_permission_rationale_message")
                        .setNegativeButton(
                            android.R.string.cancel,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                                token?.cancelPermissionRequest()
                            })
                        .setPositiveButton(
                            android.R.string.ok,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                                token?.continuePermissionRequest()
                            })
                        .show()
                }

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "hihi", Toast.LENGTH_LONG).show()
                }

            }).check()
    }
}