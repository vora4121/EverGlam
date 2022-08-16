package com.android.everglam.ui.base

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.everglam.R
import com.android.everglam.utils.AppPreferencesHelper

abstract class BaseActivity : AppCompatActivity() {

    val loaderDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage(getString(R.string.loading))
            //  setCancelable(false)
        }
    }
    private lateinit var appPreferencesHelper: AppPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPreferencesHelper = AppPreferencesHelper(this, getString(R.string.app_name))
    }

    fun getAppPreferencesHelper(): AppPreferencesHelper {
        return appPreferencesHelper
    }

    val pref: AppPreferencesHelper by lazy {
        getAppPreferencesHelper()
    }

    fun showLoader() {
        loaderDialog.show()
    }

    fun hideLoader() {
        loaderDialog.dismiss()
    }
}