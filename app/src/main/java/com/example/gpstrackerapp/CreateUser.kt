package com.example.gpstrackerapp

class CreateUser {
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit var issharing: String
    lateinit var code: String
    var lat: Double? = null
    var lng: Double? = null
    lateinit var imageUrl: String
    lateinit var userid: String
    var online: String? = null

    public constructor()
    constructor(
        name: String,
        email: String,
        password: String,
        issharing: String,
        code: String,
        lat: Double,
        lng: Double,
        imageUrl: String,
        userid: String,
        online: String
    ) {
        this.name = name
        this.email = email
        this.password = password
        this.issharing = issharing
        this.code = code
        this.lat = lat
        this.lng = lng
        this.imageUrl = imageUrl
        this.userid = userid
        this.online = online
    }
}