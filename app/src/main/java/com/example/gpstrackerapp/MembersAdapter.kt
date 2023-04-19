package com.example.gpstrackerapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MembersAdapter : RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    lateinit var namelist: ArrayList<CreateUser>
    lateinit var c: Context

    constructor(namelist: ArrayList<CreateUser>, c: Context) {
        this.namelist = namelist
        this.c = c
    }

    class MembersViewHolder : RecyclerView.ViewHolder, View.OnClickListener  {
        lateinit var name_txt: TextView
        lateinit var offline: ImageView
        lateinit var un_friend: ImageView
        lateinit var circleImageView: CircleImageView
        lateinit var c: Context
        lateinit var nameArrayList: ArrayList<CreateUser>
        lateinit var auth: FirebaseAuth
        lateinit var user: FirebaseUser

        constructor(
            itemView: View,
            c: Context,
            nameArrayList: ArrayList<CreateUser>
        ) : super(itemView) {
            this.c = c
            this.nameArrayList = nameArrayList

            itemView.setOnClickListener(this)
            auth = FirebaseAuth.getInstance()
            user = auth.currentUser!!

            name_txt = itemView.findViewById(R.id.item_title)
            offline = itemView.findViewById(R.id.off)
            un_friend = itemView.findViewById(R.id.unFriend)
            circleImageView = itemView.findViewById(R.id.circleimageview)

            un_friend.setOnClickListener { task ->
                var my_intent = Intent(c, MyCircleActivity::class.java)
                my_intent.putExtra("delete_friend", nameArrayList.get(position).userid)
                my_intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                c.startActivity(my_intent)
            }

        }

        override fun onClick(v: View?) {
//            truyền lat và lng qua activity UserLocationMain
            var infor_loca = nameArrayList.get(position)

            var intent = Intent(c, UserLocationMainActivity::class.java)
            intent.putExtra("lat", infor_loca.lat)
            intent.putExtra("lng", infor_loca.lng)
            intent.putExtra("click", "clicked")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            c.startActivity(intent)

//            Toast.makeText(c, "Successfull", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        var membersViewHolder: MembersViewHolder = MembersViewHolder(v, c, namelist)

        return membersViewHolder
    }

    override fun getItemCount(): Int {
        return namelist.size
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        var currentUserObj: CreateUser = namelist.get(position)

        // Cập nhật trạng thái online/offline của người dùng
        if (currentUserObj.online == "true") {
            Picasso.get().load(R.drawable.green_online).into(holder.offline)
        } else {
            Picasso.get().load(R.drawable.red_offline).into(holder.offline)
        }
        holder.name_txt.setText(currentUserObj.name)
        Picasso.get().load(currentUserObj.imageUrl).placeholder(R.drawable.avatar)
            .into(holder.circleImageView)

    }
}
