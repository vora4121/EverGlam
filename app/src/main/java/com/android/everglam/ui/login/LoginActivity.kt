package com.android.everglam.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.android.everglam.databinding.ActivityLoginBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.ui.signup.CreateStaffAccActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.AppPreferencesHelper
import com.android.everglam.utils.goTo
import com.android.everglam.utils.showShortSnack
import com.google.firebase.database.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private var database: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inItListener()

    }

    private fun inItListener() {
        database = FirebaseDatabase.getInstance().getReference(AppConstant.STAFF_ACCOUNT_TABLE)
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v!!) {

            binding.btnLogin -> {
                if (validate()) {
                    validateLogin()
                }
            }
        }
    }

    private fun validateLogin() {

        showLoader()

        val queryEmail: Query = database?.orderByChild("staff_email")!!.equalTo(binding.edtEmail.text.toString())

        queryEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val queryPassword: Query = database?.orderByChild("staff_pass")!!
                        .equalTo(binding.edtPass.text.toString())
                    queryPassword.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            hideLoader()
                            if (snapshot.exists()) {
                                for (snap in snapshot.children) {
                                    if (snap.child("staff_email").value.toString() == "rinchumathew@yahoo.com" &&
                                        snap.child("staff_pass").value.toString() == "Everglam@1603" &&
                                        snap.child("staff_name").value.toString() == "Rinchu Mathew") {
                                        pref.setBoolean(AppPreferencesHelper.USER_IS_LOGIN, true)
                                        pref.setString(AppPreferencesHelper.USER_TYPE, AppConstant.USER_TYPE_ADMIN)
                                        goTo(DashboardActivity::class.java)
                                    } else {
                                        pref.setBoolean(AppPreferencesHelper.USER_IS_LOGIN, true)
                                        pref.setString(AppPreferencesHelper.USER_TYPE, AppConstant.USER_TYPE_STAFF)
                                        goTo(DashboardActivity::class.java)
                                    }
                                    break
                                }

                            } else {
                                hideLoader()
                                showShortSnack(binding.root, "User not exist")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            hideLoader()
                            showShortSnack(binding.root, error.message)
                        }
                    })
                } else {
                    hideLoader()
                    showShortSnack(binding.root, "Email not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoader()
                showShortSnack(binding.root, error.message)
            }
        })
    }

    private fun validate(): Boolean {

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