package com.android.everglam.ui.usertype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.everglam.R
import com.android.everglam.databinding.ActivitySelectUserTypeBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.ui.login.LoginActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.AppPreferencesHelper
import com.android.everglam.utils.goTo

class SelectUserTypeActivity : BaseActivity(), View.OnClickListener {

    val binding : ActivitySelectUserTypeBinding by lazy { ActivitySelectUserTypeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        initListener()

    }

    private fun initListener() {
        binding.btnAdmin.setOnClickListener(this)
        binding.btnStaff.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!){

            binding.btnAdmin -> {
                goTo(LoginActivity::class.java)
            }

            binding.btnStaff -> {
                goTo(LoginActivity::class.java)
            }

        }
    }

}