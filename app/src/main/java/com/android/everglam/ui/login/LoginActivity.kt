package com.android.everglam.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.android.everglam.R
import com.android.everglam.databinding.ActivityLoginBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.AppPreferencesHelper
import com.android.everglam.utils.goTo
import com.android.everglam.utils.showShortSnack

class LoginActivity : BaseActivity(), View.OnClickListener {

    val binding : ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inItListener()

    }

    private fun inItListener() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v!!){

            binding.btnLogin -> {
                if (validate()){
                    pref.setBoolean(AppPreferencesHelper.USER_IS_LOGIN, true)
                    if (pref.getString(AppPreferencesHelper.USER_TYPE,"").equals(AppConstant.USER_TYPE_ADMIN)) {
                        goTo(DashboardActivity::class.java)
                    }else{
                        goTo(DashboardActivity::class.java)
                    }
                }
            }
        }
    }


    private fun validate() : Boolean{

        return when {
            TextUtils.isEmpty(binding.edtEmail.text.toString()) -> {
                showShortSnack(binding.root, "Please enter your Email")
                false
            }
            TextUtils.isEmpty(binding.edtPass.text.toString()) -> {
                showShortSnack(binding.root, "Please enter your Password")
                false
            }
            else -> {
                true
            }
        }
    }
}