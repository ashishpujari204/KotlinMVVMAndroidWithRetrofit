package com.app.smartprocessors.ui.login

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("auth_token")
    var authToken: String = "",
    @SerializedName("blockUser")
    var blockUser: String = "",
    @SerializedName("mSiteId")
    var mSiteId: String = "",
    @SerializedName("mSiteName")
    var mSiteName: String = "",
    @SerializedName("mobile")
    var mobile: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("role")
    var role: Int = 0,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("user_id")
    var userId: String = "",
    @SerializedName("app_url")
    var app_url: String = ""
)