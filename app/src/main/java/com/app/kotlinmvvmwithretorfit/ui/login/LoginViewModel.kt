package com.app.smartprocessors.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.kotlinmvvmwithretorfit.rest.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : AndroidViewModel {

    private var validationClass: ValidationClass

    constructor(application: Application) : super(application) {
        validationClass = ValidationClass(application)
    }

    fun validateCredentials(mobile: String, password: String): LiveData<String> {
        return validationClass.validateCredentials(mobile, password)
    }

    fun callLoginAPI(mobile: String, password: String): MutableLiveData<User> {
        var userData = MutableLiveData<User>()
        val userCall: Call<User> = ApiClient.getClient.login(mobile, password)
        userCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userData.value = response.body()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                userData.value = null
            }
        })
        return userData

    }
}