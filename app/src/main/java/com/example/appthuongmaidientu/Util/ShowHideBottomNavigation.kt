package com.example.appthuongmaidientu.Util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.appthuongmaidientu.Activities.ShoppingActivity
import com.example.appthuongmaidientu.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility= View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility= View.VISIBLE
}