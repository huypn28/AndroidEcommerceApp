package com.example.appthuongmaidientu.Util

import android.provider.ContactsContract.CommonDataKinds.Email

sealed class RegisterValidation(){
    object  Success: RegisterValidation()
    data class Failed(val  message: String):RegisterValidation()
}

data class RegisterFieldState(
    val email: RegisterValidation,
    val password: RegisterValidation
)
