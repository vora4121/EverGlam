package com.android.everglam.ui.signup

import android.R.id
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.everglam.data.StaffModel
import com.android.everglam.databinding.ActivityCreateStaffAccBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.utils.AppConstant
import com.android.everglam.utils.showOptionAlert
import com.android.everglam.utils.showShortSnack
import com.google.firebase.database.*

class CreateStaffAccActivity : BaseActivity(), View.OnClickListener {

    val binding: ActivityCreateStaffAccBinding by lazy {
        ActivityCreateStaffAccBinding.inflate(
            layoutInflater
        )
    }

    private var database: DatabaseReference? = null
    var staffModel: StaffModel? = null
    private var arrStaffList = ArrayList<StaffModel>()
    private lateinit var staffAdapter: StaffListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.btnCreateAcc.setOnClickListener(this)
        database = FirebaseDatabase.getInstance().getReference(AppConstant.STAFF_ACCOUNT_TABLE)
        binding.rvStaffList.layoutManager = LinearLayoutManager(this@CreateStaffAccActivity)
        binding.rvStaffList.addItemDecoration(DividerItemDecoration(this@CreateStaffAccActivity, DividerItemDecoration.VERTICAL))
        binding.rvStaffList.setHasFixedSize(true)

        staffAdapter = StaffListAdapter {
            showOptionAlert(
                "Alert!",
                "Are you sure you want to delete this Account?",
                "Yes"
            ) {
                deleteStaffAccount(it)
            }

        }
        binding.rvStaffList.adapter = staffAdapter
        getStaffData()

    }

    private fun getStaffData() {

        showLoader()

        arrStaffList.clear()

        database?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoader()
                if (snapshot.exists()){
                    for (postSnapshot in snapshot.children) {
                        arrStaffList.add(StaffModel(postSnapshot.child("staff_name").value.toString(),
                            postSnapshot.child("staff_email").value.toString(),
                            postSnapshot.child("staff_pass").value.toString(),
                            postSnapshot.child("staff_key").value.toString()
                        ))
                    }
                }
                staffAdapter.addItem(arrStaffList)
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoader()
                showShortSnack(binding.root, error.message)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!) {
            binding.btnCreateAcc -> {
                if (validate()) {
                    createStaffAccount()
                }
            }
        }
    }

    private fun createStaffAccount() {

        val id = database!!.push().key.toString()

        staffModel = StaffModel(
            binding.edtName.text.toString(),
            binding.edtEmail.text.toString(),
            binding.edtPassword.text.toString(),
            id
        )

        showLoader()

        database?.child(id)?.setValue(staffModel)
            ?.addOnSuccessListener {
                showShortSnack(binding.root, "Success")
                getStaffData()
                hideLoader()
            }?.addOnFailureListener {
                showShortSnack(binding.root, it.message.toString())
                hideLoader()
            }
    }

    private fun deleteStaffAccount(staffModel: StaffModel) {
        showLoader()
        database?.child(staffModel.staff_key)?.removeValue()?.addOnSuccessListener {
            showShortSnack(binding.root, "Delete success")
            getStaffData()
        }?.addOnFailureListener {
            showShortSnack(binding.root, "Something went wrong")
            hideLoader()
        }
    }

    fun validate(): Boolean {

        if (TextUtils.isEmpty(binding.edtName.text.toString())) {
            showShortSnack(binding.root, "Please enter Staff Name")
            return false
        } else if (TextUtils.isEmpty(binding.edtEmail.text.toString())) {
            showShortSnack(binding.root, "Please enter Staff Email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()) {
            showShortSnack(binding.root, "Please enter Valid Email")
            return false
        } else if (TextUtils.isEmpty(binding.edtPassword.text.toString())) {
            showShortSnack(binding.root, "Please enter Password")
            return false
        } else {
            return true
        }
    }

}