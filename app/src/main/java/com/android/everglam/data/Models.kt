package com.android.everglam.data

import java.io.Serializable

data class StaffModel(
    val staff_name: String = "",
    val staff_email: String = "",
    val staff_pass: String = "",
    val staff_key: String = ""
)

data class ScannedData(
    val Category: String = "",
    val MRP: String = "",
    val Product_Name: String = "",
    val Qty_Available: String = "",
    val Sub_Category_Brand: String = "",
    val Retail_Price: String = "",
    val Discount: String = "",
    val BarCode : String = ""
) : Serializable
