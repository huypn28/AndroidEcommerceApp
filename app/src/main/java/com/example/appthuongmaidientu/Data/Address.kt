package com.example.appthuongmaidientu.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTiTle:String,
    val fullName:String,
    val address:String,
    val phone:String,
    val note:String,

):Parcelable{
    constructor(): this("","","","","")
}
