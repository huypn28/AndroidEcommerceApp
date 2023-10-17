package com.example.appthuongmaidientu.Util

import android.util.Patterns
import java.util.regex.Pattern

fun validateEmail(email: String): RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email không thể bỏ trống")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Không phải email")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Mật khẩu không thể bỏ trống")

    if (password.length<8)
        return RegisterValidation.Failed("Mật khẩu phải có 8 ký tự")

    return RegisterValidation.Success
}
