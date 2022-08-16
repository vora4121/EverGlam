package com.android.everglam.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.everglam.databinding.ActivityDashboardBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.scanner.ScannerActivity
import com.android.everglam.ui.searchedproduct.SearchedProductActivity
import com.android.everglam.ui.signup.CreateStaffAccActivity
import com.android.everglam.utils.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class DashboardActivity : BaseActivity(), View.OnClickListener {

    private val binding: ActivityDashboardBinding by lazy {
        ActivityDashboardBinding.inflate(
            layoutInflater
        )
    }
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var isPermissionGrant: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewListeners()
        onPermission()
    }

    private fun initViewListeners() {

        binding.btnScanCode.setOnClickListener(this)
        binding.btnSearchProduct.setOnClickListener(this)
        binding.btnTypeCode.setOnClickListener(this)
        binding.btnUploadData.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.imgClose.setOnClickListener(this)
        binding.btnCreateAccount.setOnClickListener(this)

        if (pref.getString(AppPreferencesHelper.USER_TYPE, "") == AppConstant.USER_TYPE_ADMIN) {
            binding.clAdminView.visibility = View.VISIBLE
            binding.clMainView.visibility = View.GONE
        } else {
            binding.clAdminView.visibility = View.GONE
            binding.clMainView.visibility = View.VISIBLE
        }

        activityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.e("==>>", "initViewListeners: " + result.data!!.data!!)
                }
            }
    }

    override fun onClick(v: View?) {

        when (v!!) {

            binding.btnScanCode -> {
                if (isPermissionGrant) {
                    goTo(ScannerActivity::class.java)
                } else {
                    onPermission()
                }
            }

            binding.btnSearchProduct -> {
                goTo(SearchedProductActivity::class.java)
            }

            binding.btnTypeCode -> {

                binding.clMainView.visibility = View.GONE
                binding.clCodeSearchView.visibility = View.VISIBLE

            }

            binding.btnSearch -> {
                binding.clMainView.visibility = View.VISIBLE
                binding.clCodeSearchView.visibility = View.GONE
            }

            binding.imgClose -> {
                binding.clMainView.visibility = View.VISIBLE
                binding.clCodeSearchView.visibility = View.GONE
            }

            binding.btnCreateAccount -> {
                goTo(CreateStaffAccActivity::class.java)
            }

            binding.btnUploadData -> {
                if (isPermissionGrant) {
                    activityResult.launch(
                        Intent.createChooser(
                            Intent().setAction(Intent.ACTION_GET_CONTENT).setType("file/*"),
                            "Select data"
                        )
                    )
                } else {
                    onPermission()
                }

                Log.e("TAG", "onClick: ")

            }
        }
    }

    private fun onPermission() {
        Dexter.withContext(this@DashboardActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    isPermissionGrant = true
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                    showOptionAlert(
                        "Permission Needed",
                        "Common Core Prep needs storage permission to access your storage.",
                        "OKAY"
                    ) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                list: List<PermissionRequest>,
                permissionToken: PermissionToken
            ) {
                permissionToken.continuePermissionRequest()
            }
        }).check()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}