package com.app.smartprocessors.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ValidationClass(application: Application) {
    var application: Application = application

    fun validateCredentials(mobile:String,password:String): LiveData<String> {
        val loginErrorMessage = MutableLiveData<String>()
        if(isPhoneValid(mobile)){
            if(password.isEmpty() || password.length<5 ){
                loginErrorMessage.value = "Invalid Password"
            }else{
                loginErrorMessage.value = "success"
            }
        }else{
            loginErrorMessage.value = "Invalid mobile number"
        }

        return  loginErrorMessage
    }



    private fun isPhoneValid(mobile: String): Boolean {

        if(mobile.length==10){
            return true
        }
        return false
    }

}