package com.android.everglam.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.everglam.R
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.ui.login.LoginActivity
import com.android.everglam.ui.signup.CreateStaffAccActivity
import com.android.everglam.ui.usertype.SelectUserTypeActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.AppPreferencesHelper
import com.android.everglam.utils.goTo

class SplashActivity : BaseActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {

            if (pref.getBoolean(AppPreferencesHelper.USER_IS_LOGIN, false) == true){
                if (pref.getString(AppPreferencesHelper.USER_TYPE, "").equals(AppConstant.USER_TYPE_ADMIN)){
                    goTo(DashboardActivity::class.java)
                }else{
                    goTo(DashboardActivity::class.java)
                }
            }else{
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        handler.postDelayed(runnable, 3000)

    }

    override fun onStop() {
        handler.removeCallbacks(runnable)
        super.onStop()
    }


}