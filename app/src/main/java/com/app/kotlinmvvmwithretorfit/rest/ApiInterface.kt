package com.app.kotlinmvvmwithretorfit.rest


import com.app.smartprocessors.ui.login.User
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("LoginSmart.php")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ): Call<User>


}