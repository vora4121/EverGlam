package com.android.everglam.ui.signup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.everglam.data.StaffModel
import com.android.everglam.databinding.ItemStaffBinding

class StaffListAdapter(private var listener: (staffModel: StaffModel) -> Unit) :
    RecyclerView.Adapter<StaffListAdapter.ViewHolder>() {

    private var arrStaffList = ArrayList<StaffModel>()

    fun addItem(arrStaffList: ArrayList<StaffModel>) {
        this.arrStaffList.clear()
        this.arrStaffList.addAll(arrStaffList)
        notifyDataSetChanged()
    }

    class ViewHolder(
        val binding: ItemStaffBinding,
        private var listener: (staffModel: StaffModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(staffModel: StaffModel){

            binding.tvName.text = staffModel.staff_name
            binding.tvEmail.text = staffModel.staff_email

            binding.imgDeleteUser.setOnClickListener {
                listener.invoke(staffModel)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemStaffBinding = ItemStaffBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrStaffList[position])
    }

    override fun getItemCount() : Int = arrStaffList.size

}