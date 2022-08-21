package com.android.everglam.ui.searchedproduct

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.everglam.R
import com.android.everglam.data.ScannedData
import com.android.everglam.databinding.ActivitySearchedProductBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.productdetail.ProductDetailsAdapter
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.showShortSnack
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class SearchedProductActivity : BaseActivity(), View.OnClickListener {

    val binding: ActivitySearchedProductBinding by lazy { ActivitySearchedProductBinding.inflate(layoutInflater) }
    private var database: DatabaseReference? = null
    private var productDetailsAdapter: ProductDetailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inItViews()
    }

    private fun inItViews() {
        database = FirebaseDatabase.getInstance().getReference(AppConstant.DATABASE_TABLE)
        binding.btnSearch.setOnClickListener(this)

        binding.rvProductSearch.layoutManager = LinearLayoutManager(this@SearchedProductActivity)
        productDetailsAdapter = ProductDetailsAdapter()
        binding.rvProductSearch.adapter = productDetailsAdapter

        if (AppConstant.arrProductData.isEmpty()) {
            searchProduct()
        } else {
            setUpRecyclerView()
        }

        binding.etSearchProduct.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()){
                    searchProduct(s.toString())
                }else{
                    productDetailsAdapter?.addItem(AppConstant.arrProductData)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!) {
            binding.btnSearch -> {
                if (TextUtils.isEmpty(binding.etSearchProduct.text.toString())) {
                    showShortSnack(binding.root, "Please enter product name")
                } else {
                    searchProduct()
                }
            }
        }
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
                                datasnap.child("Discount").value.toString()
                            )
                        )
                    }
                    setUpRecyclerView()
                } else {
                    showShortSnack(binding.root, "Search product not available. Please re-scan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchedProductActivity, error.message, Toast.LENGTH_SHORT).show()
                hideLoader()
            }
        })
    }

    private fun setUpRecyclerView() {
        productDetailsAdapter!!.addItem(AppConstant.arrProductData)
    }

    private fun searchProduct(name : String){

        val arrSearchedProduct : ArrayList<ScannedData> = ArrayList()

        for (scaned in AppConstant.arrProductData){
            if (scaned.Product_Name.lowercase(Locale.getDefault()).contains(name)){
                arrSearchedProduct.add(scaned)
            }
        }

        productDetailsAdapter?.searchData(arrSearchedProduct)
    }

}