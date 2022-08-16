package com.android.everglam.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.everglam.R
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.ui.usertype.SelectUserTypeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            val intent = Intent(this@SplashActivity, SelectUserTypeActivity::class.java)
            startActivity(intent)
            finish()
        }

        handler.postDelayed(runnable, 3000)

    }

    override fun onStop() {
        handler.removeCallbacks(runnable)
        super.onStop()
    }

}