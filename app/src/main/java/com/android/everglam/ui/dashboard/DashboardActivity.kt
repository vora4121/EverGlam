package com.android.everglam.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.everglam.data.ScannedData
import com.android.everglam.databinding.ActivityDashboardBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.productdetail.ScanedProductDetailsActivity
import com.android.everglam.ui.scanner.ScannerActivity
import com.android.everglam.ui.searchedproduct.SearchedProductActivity
import com.android.everglam.ui.signup.CreateStaffAccActivity
import com.android.everglam.utils.*
import com.google.firebase.database.*
import com.google.gson.Gson
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
    private var database: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference(AppConstant.DATABASE_TABLE)
        initViewListeners()
        onPermission()
    }

    private fun initViewListeners() {
        binding.btnScanCode.setOnClickListener(this)
        binding.btnSearchProduct.setOnClickListener(this)
        binding.btnTypeCode.setOnClickListener(this)
        binding.btnScannedByAdmin.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.imgClose.setOnClickListener(this)
        binding.btnCreateAccount.setOnClickListener(this)
        binding.btnSearchCodeByAdmin.setOnClickListener(this)
        binding.btnSearchProductByAdmin.setOnClickListener(this)

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

        if (AppConstant.arrProductData.isEmpty()) {
            searchProduct()
//          clearDataBase()
        }


    }

    private fun clearDataBase() {
        showLoader()
        database?.removeValue()?.addOnSuccessListener {
            showShortSnack(binding.root, "Database has been cleared! Now you can upload new data")
            hideLoader()
        }?.addOnFailureListener {
            showShortSnack(binding.root, "Something went wrong")
            hideLoader()
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

            binding.btnSearchProductByAdmin -> {
                goTo(SearchedProductActivity::class.java)
            }

            binding.btnTypeCode -> {
                binding.clMainView.visibility = View.GONE
                binding.clCodeSearchView.visibility = View.VISIBLE
            }

            binding.btnSearchCodeByAdmin -> {
                binding.clAdminView.visibility = View.GONE
                binding.clCodeSearchView.visibility = View.VISIBLE
            }

            binding.btnSearch -> {
                if (TextUtils.isEmpty(binding.etCode.text.toString())) {
                    showShortSnack(binding.root, "Please enter code")
                } else {
                    goToWithBundle(ScanedProductDetailsActivity::class.java) {
                        putString("Result", binding.etCode.text.toString())
                    }
                }
            }

            binding.imgClose -> {
                if (pref.getString(AppPreferencesHelper.USER_TYPE, "") == AppConstant.USER_TYPE_ADMIN) {
                    binding.clAdminView.visibility = View.VISIBLE
                    binding.clCodeSearchView.visibility = View.GONE
                }else{
                    binding.clMainView.visibility = View.VISIBLE
                    binding.clCodeSearchView.visibility = View.GONE
                }
            }

            binding.btnCreateAccount -> {
                goTo(CreateStaffAccActivity::class.java)
            }

            binding.btnScannedByAdmin -> {
                if (isPermissionGrant) {
                    goTo(ScannerActivity::class.java)
                } else {
                    onPermission()
                }
            }
        }
    }

    private fun onPermission() {
        Dexter.withContext(this@DashboardActivity).withPermissions(
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    isPermissionGrant = true
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                    showOptionAlert(
                        "Permission Needed",
                        "EverGlam needs Camera permission for scanning Code.",
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

    private fun searchProduct() {
        showLoader()
        database?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoader()
                if (snapshot.exists()) {
                    for (datasnap in snapshot.children) {
                        AppConstant.arrProductData.add(
                            ScannedData(
                                datasnap.child("Category").value.toString(),
                                datasnap.child("MRP").value.toString(),
                                datasnap.child("Product Name").value.toString(),
                                datasnap.child("Qty_Available").value.toString(),
                                datasnap.child("Sub Category_Brand").value.toString(),
                                datasnap.child("Retail Price").value.toString(),
                                datasnap.child("Discount").value.toString(),
                                datasnap.child("Barcode").value.toString()
                            )
                        )
                    }
                } else {
                    showShortSnack(binding.root, "Search product not available. Please re-scan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                hideLoader()
            }
        })
    }


}