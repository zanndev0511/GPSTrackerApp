package com.example.gpstrackerapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso

class UserLocationMainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    lateinit var mMap: GoogleMap
    lateinit var client: GoogleApiClient
    lateinit var request: LocationRequest
    lateinit var latLng: LatLng
    var lat: Double? = null
    var lng: Double? = null
    lateinit var databaseReference: DatabaseReference
    lateinit var user: FirebaseUser
    lateinit var current_user_name: String
    lateinit var current_user_email: String
    lateinit var current_user_imageUrl: String
    lateinit var t1_currentName: TextView
    lateinit var t2_currentEmail: TextView
    lateinit var i1: ImageView
    lateinit var butMyLoc: Button
    var currentMarker: Marker? = null

    companion object

    var isSatelliteMapEnabled: Boolean? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        // Thêm node mới vào cơ sở dữ liệu
//                        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val userRef =
            database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid.toString())
                .child("online")
        userRef.setValue("true")
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val database = FirebaseDatabase.getInstance()
            try {
                val userRef =
                    database.getReference("Users/" + FirebaseAuth.getInstance().currentUser!!.uid + "/online")
                userRef.removeValue()
            } catch (e: Exception) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_location_main)
        var inflatedView: View = layoutInflater.inflate(R.layout.header_menu, null)


        butMyLoc = findViewById<Button?>(R.id.currentLoc)

        t1_currentName = findViewById(R.id.title_text)
        t2_currentEmail = findViewById(R.id.email_text)
        navigationView = findViewById(R.id.nav_view)

        var header: View = navigationView.getHeaderView(0)
        i1 = findViewById(R.id.imageView)
        i1.setOnClickListener { task ->
            var intent_i1 = Intent(this, InformationUserActivity::class.java)
            startActivity(intent_i1)
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                current_user_name =
                    dataSnapshot.child(user.uid).child("name").getValue(String::class.java)
                        .toString()
                current_user_email =
                    dataSnapshot.child(user.uid).child("email").getValue(String::class.java)
                        .toString()
                current_user_imageUrl =
                    dataSnapshot.child(user.uid).child("imageUrl").getValue(String::class.java)
                        .toString()

                t1_currentName.setText(current_user_name)
                t2_currentEmail.setText(current_user_email)

                Picasso.get().load(current_user_imageUrl).into(i1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        drawerLayout = findViewById(R.id.draw_layout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_joinCircle -> {
                    var myIntent: Intent = Intent(this, JoinCircleActivity::class.java)
                    startActivity(myIntent)
                }
                R.id.nav_myCircle -> {
                    var myIntent: Intent = Intent(this, MyCircleActivity::class.java)
                    startActivity(myIntent)
                }
                R.id.nav_inviteMembers -> {
                    var myIntent: Intent = Intent(this, InviteMemberActivity::class.java)
                    startActivity(myIntent)
                }
                R.id.nav_shareLoc -> {
                    var myIntent: Intent = Intent(Intent.ACTION_SEND)
                    myIntent.setType("text/plain")
                    myIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "My Location is: " + "https://www.google.com/maps/0" + latLng.latitude + "," + latLng.longitude + ",17z"
                    )
                    startActivity(Intent.createChooser(myIntent, "Share using: "))
                }
                R.id.nav_signOut -> {
                    var user: FirebaseUser? = auth.currentUser
                    if (user != null) {
                        // Xóa node khi người dùng đăng xuất
                        FirebaseAuth.getInstance().addAuthStateListener { auth ->
                            val database = FirebaseDatabase.getInstance()
                            val userRef = database.getReference("Users/" + user!!.uid + "/online")
                            userRef.removeValue()
                        }
                        auth.signOut()
                        finish()
                        var myIntent: Intent = Intent(this, MainActivity::class.java)
                        startActivity(myIntent)
                    }
                }
                R.id.nav_map -> {
                    var intent: Intent = Intent(this, MapCustomizationActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Khởi tạo đối tượng LatLng cho vị trí của Việt Nam
        val vietnamLatLng = LatLng(14.0583, 108.2772)

        // Thiết lập bản đồ hiển thị ở vị trí này và thu phóng một chút
        val cameraUpdate = CameraPosition.Builder()
            .target(vietnamLatLng)
            .zoom(5f)
            .tilt(30f)
            .bearing(0f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraUpdate))

//        Người dùng chọn bản đồ
        var map_custom = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("map_custom")
        map_custom.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue().toString().equals("satelite")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID) // Chuyển sang bản đồ vệ tinh
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL) // Chuyển sang bản đồ vệ tinh
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        client = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        client.connect()

        mMap.uiSettings.isZoomControlsEnabled = true

// Hiển thị vị trí của bạn bè khi nhấn ở card_layout
        var intent = intent
        var lat = intent.getDoubleExtra("lat", 0.00000000000000000000000000000000)
        var lng = intent.getDoubleExtra("lng", 0.00000000000000000000000000000000)
        Log.d("lat", lat.toString())
        var locat = LatLng(lat, lng)
        var check: String = "clicked"
        if (check.equals(intent.getStringExtra("click"))) {
            location(locat)
        }
    }

    override fun onConnected(p0: Bundle?) {
        request = LocationRequest.create()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.setInterval(3000)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this)
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {
        var updateLocaLat =
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)
                .child("lat").setValue(location.latitude)
        var updateLocaLng =
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)
                .child("lng").setValue(location.longitude)
        if (location == null) {
            Toast.makeText(applicationContext, "Could not get location", Toast.LENGTH_SHORT).show()
        } else {
            latLng = LatLng(location.latitude, location.longitude)
            var options = MarkerOptions()
            options.position(latLng)
            options.title("Current Location")

            butMyLoc.setOnClickListener { task ->
                getMyLocation(latLng)
            }

//            Lấy điểm đánh dấu của bạn bè

            var member =
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.uid)
                    .child("CircleMembers")
            member.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val child = ds.key
                        Log.i("snap", child.toString())
                        var membercheck =
                            FirebaseDatabase.getInstance().getReference().child("Users")
                        membercheck.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                for (ds1 in snapshot1.children) {
                                    val child1 = ds1.key
                                    Log.d("user", child1.toString())
                                    if (child == child1) {
                                        var inforMemberCirLat =
                                            FirebaseDatabase.getInstance().getReference()
                                                .child("Users").child(child.toString()).child("lat")
                                        inforMemberCirLat.addValueEventListener(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot2: DataSnapshot) {
                                                var latmember =
                                                    snapshot2.getValue().toString().toDouble()
                                                var inforMemberCirLng =
                                                    FirebaseDatabase.getInstance().getReference()
                                                        .child("Users").child(child.toString())
                                                        .child("lng")
                                                inforMemberCirLng.addValueEventListener(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot3: DataSnapshot) {
                                                        var lngmember =
                                                            snapshot3.getValue().toString()
                                                                .toDouble()
                                                        var memberCir_mail =
                                                            membercheck.child(child.toString())
                                                                .child("email")
                                                        memberCir_mail.addListenerForSingleValueEvent(
                                                            object : ValueEventListener {
                                                                override fun onDataChange(snapshot4: DataSnapshot) {
                                                                    val memberCir_lo =
                                                                        LatLng(latmember, lngmember)
                                                                    mMap.addMarker(
                                                                        MarkerOptions()
                                                                            .position(memberCir_lo)
                                                                            .title(
                                                                                snapshot4.getValue()
                                                                                    .toString()
                                                                            )
                                                                    )

                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                            })
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        TODO("Not yet implemented")
                                                    }
                                                })
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                    var createUser: CreateUser? = null
                    for (childDss: DataSnapshot in snapshot.children) {
                        createUser = childDss.getValue(CreateUser::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            if (currentMarker != null) {
                currentMarker!!.remove()
            }
            currentMarker = mMap.addMarker(options)
        }

    }

    fun location(latLng: LatLng) {
//        var intent = intent
//        var lat = intent.getDoubleExtra("lat", 0.00000000000000000000000000000000)
//        var lng = intent.getDoubleExtra("lng", 0.00000000000000000000000000000000)
//        var locat = LatLng(lat, lng)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(15f)
            .tilt(30f)
            .bearing(0f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun getMyLocation(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(15f)
            .tilt(30f)
            .bearing(0f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}