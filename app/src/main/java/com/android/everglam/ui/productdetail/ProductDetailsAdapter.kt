package com.android.everglam.ui.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.everglam.data.ScannedData
import com.android.everglam.databinding.ItemProductDetailsBinding

class ProductDetailsAdapter : RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder>() {

    private var arrScannedData = ArrayList<ScannedData>()

    fun addItem(arrScannedList: ArrayList<ScannedData>) {
        this.arrScannedData.clear()
        this.arrScannedData.addAll(arrScannedList)
        notifyDataSetChanged()
    }


    class ViewHolder(
        val binding: ItemProductDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scannedData: ScannedData){

            val EgPrice = scannedData.MRP -  (scannedData.MRP * scannedData.Discount) / 100

            binding.tvName.text = scannedData.Product_Name
            binding.tvQty.text = scannedData.Qty_Available.toString()
            binding.tvMRP.text = scannedData.MRP.toString()
            binding.tvEGPrice.text = EgPrice.toString()
            binding.tvDiscount.text = scannedData.Discount.toString()

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
}