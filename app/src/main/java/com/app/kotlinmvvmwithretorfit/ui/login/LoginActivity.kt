package com.app.kotlinmvvmwithretorfit.ui.login

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.kotlinmvvmwithretorfit.R
import com.app.smartprocessors.ui.baseactivity.BaseActivity
import com.app.smartprocessors.ui.login.LoginViewModel
import com.app.smartprocessors.ui.login.User
import com.app.smartprocessors.util.Constants
import com.app.kotlinmvvmwithretorfit.util.Util
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this@LoginActivity).get(LoginViewModel::class.java)

        if (null != Constants.getUserName(this@LoginActivity)) {
            etMobileNumber.setText(Constants.getUserName(this@LoginActivity))
        }

        var way=intent.getStringExtra("WAY");
        if(way!=null){
            val appPackageName = packageName // package name of the app

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
        buttonLogin.setOnClickListener {
            if (Util.verifyAvailableNetwork(this)) {
                loginViewModel.validateCredentials(
                    etMobileNumber.text.toString(),
                    etPassword.text.toString()
                ).observe(this@LoginActivity, Observer<String> { t ->
                    if (t.toString() == "success") {
                        callLoginAPI(etMobileNumber.text, etPassword.text)
                    } else {
                        showToast(t)
                    }
                })
            } else {
                showToast(getString(R.string.internet_connection))
            }
        }
        btnForgotPassword.setOnClickListener {

        }

    }


    private fun callLoginAPI(mobile: Editable?, password: Editable?) {
        showProgressDialog()
        loginViewModel.callLoginAPI(mobile.toString(), password.toString())
            .observe(this@LoginActivity, Observer<User> {
                stopProgressDialog()
                if (it != null && it.blockUser.isNotEmpty()) {
                    Constants.saveUser(this@LoginActivity, it)
                    var loginIntent: Intent? = null

                    if (it.blockUser == "0") {
                        when (it.role) {

                        }
                    } else {
                        showToast("Please contact your admin team")
                    }

                    startActivity(loginIntent)
                    finish()
                } else {
                    showToast("Invalid username or password")
                }
            })
    }


}
