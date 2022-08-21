package com.android.everglam.ui.productdetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.everglam.data.ScannedData
import com.android.everglam.databinding.ItemProductDetailsBinding
import java.text.DecimalFormat

class ProductDetailsAdapter : RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder>() {

    private var arrScannedData = ArrayList<ScannedData>()

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(arrScannedList: ArrayList<ScannedData>) {
        this.arrScannedData.clear()
        this.arrScannedData.addAll(arrScannedList)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemProductDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scannedData: ScannedData){
            val EgPrice = ((scannedData.MRP.toDouble() - scannedData.Retail_Price.toDouble()).toDouble() / scannedData.MRP.toDouble()) * 100
            binding.tvName.text = scannedData.Product_Name
            binding.tvQty.text = scannedData.Qty_Available.toString()
            binding.tvMRP.text = scannedData.MRP.toString()
            binding.tvEGPrice.text = scannedData.Retail_Price
            binding.tvDiscount.text =  DecimalFormat(".##").format(EgPrice).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemProductDetailsBinding = ItemProductDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrScannedData[position])
    }

    override fun getItemCount(): Int {
        return arrScannedData.size
    }

    fun searchData(mArrScannedData : ArrayList<ScannedData>){
        arrScannedData.clear()
        arrScannedData.addAll(mArrScannedData)
        notifyDataSetChanged()
    }

}