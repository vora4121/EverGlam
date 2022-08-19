package com.android.everglam.ui.productdetail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.everglam.data.ScannedData
import com.android.everglam.databinding.ActivityScanedProductDetailsBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.ui.scanner.ScannerActivity
import com.android.everglam.ui.searchedproduct.SearchedProductActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.goTo
import com.android.everglam.utils.showShortSnack
import com.google.firebase.database.*

class ScanedProductDetailsActivity : BaseActivity(), View.OnClickListener {

    val binding: ActivityScanedProductDetailsBinding by lazy {
        ActivityScanedProductDetailsBinding.inflate(
            layoutInflater
        )
    }

    var strResult: String = ""
    private var database: DatabaseReference? = null
    private var arrProductList: ArrayList<ScannedData>? = ArrayList()
    private var productDetailsAdapter: ProductDetailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inItSetup()
    }

    private fun inItSetup() {
        strResult = intent.getStringExtra("Result").toString()
        binding.btnScan.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        database = FirebaseDatabase.getInstance().getReference(AppConstant.DATABASE_TABLE)
        scanDataFromDatabase(strResult)
    }

    private fun scanDataFromDatabase(strScanCode: String) {
        showLoader()
        val queryEmail: Query = database?.orderByChild("Barcode")!!.equalTo(strScanCode.toDouble())
        queryEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoader()
                if (snapshot.exists()) {
                    for (datasnap in snapshot.children) {
                        arrProductList?.add(
                            ScannedData(
                                datasnap.child("Category").value.toString(),
                                datasnap.child("MRP").value.toString().toDouble(),
                                datasnap.child("Product Name").value.toString(),
                                datasnap.child("Qty_Available").value.toString().toInt(),
                                datasnap.child("Sub Category_Brand").value.toString(),
                                datasnap.child("Retail Price").value.toString().toDouble(),
                                datasnap.child("Discount").value.toString().toDouble()
                            )
                        )
                    }
                    setUpRecyclerView()
                }else{
                    showShortSnack(binding.root, "Search product not available. Please re-scan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ScanedProductDetailsActivity, error.message, Toast.LENGTH_SHORT).show()
                hideLoader()
            }
        })
    }

    private fun setUpRecyclerView() {
        if (arrProductList?.isEmpty() != true) {
            binding.rvProduct.layoutManager = LinearLayoutManager(this@ScanedProductDetailsActivity)
            productDetailsAdapter = ProductDetailsAdapter()
            binding.rvProduct.adapter = productDetailsAdapter
            productDetailsAdapter!!.addItem(arrProductList!!)
        }
    }

    override fun onClick(v: View?) {
        when(v!!){
            binding.btnScan -> {
                goTo(ScannerActivity::class.java)
            }
            binding.btnSearch -> {
                goTo(SearchedProductActivity::class.java)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goTo(DashboardActivity::class.java)
    }

}