package com.emon.composeloginvalidationdemo

import android.text.TextUtils
import android.util.Patterns

 class IoValidation {
    fun loginIoValidation(params: LoginViewModel.Param): LoginViewModel.ValidationResult {
        return if(params.email.isBlank() || !isEmailValid(params.email)) {
            LoginViewModel.ValidationResult.Failure(LoginViewModel.LoginIoResult.EmailError)
        } else if(params.password.isBlank()) {
            LoginViewModel.ValidationResult.Failure(LoginViewModel.LoginIoResult.PasswordError)
        }else{
            LoginViewModel.ValidationResult.Success
        }
    }
    private fun isEmailValid(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}