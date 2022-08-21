package com.android.everglam.ui.signup

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.everglam.data.StaffModel
import com.android.everglam.databinding.ActivityCreateStaffAccBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.dashboard.DashboardActivity
import com.android.everglam.utils.*
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
        binding.rvStaffList.addItemDecoration(
            DividerItemDecoration(
                this@CreateStaffAccActivity,
                DividerItemDecoration.VERTICAL
            )
        )
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

        database?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoader()
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {

                        if (postSnapshot.child("staff_email").value.toString() == "rinchumathew@yahoo.com" &&
                            postSnapshot.child("staff_pass").value.toString() == "Everglam@1603" &&
                            postSnapshot.child("staff_name").value.toString() == "Rinchu Mathew") {
                        }else{
                            arrStaffList.add(
                                StaffModel(
                                    postSnapshot.child("staff_name").value.toString(),
                                    postSnapshot.child("staff_email").value.toString(),
                                    postSnapshot.child("staff_pass").value.toString(),
                                    postSnapshot.child("staff_key").value.toString()
                                )
                            )
                        }
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
                    validateNewStaff()

                }
            }

            binding.btnClearData -> {
                showOptionAlert("Alert!", "Are you sure you want to Clear Database?", "Yes") {
                    clearDataBase()
                }
            }
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


    private fun validateNewStaff()  {
        showLoader()
        val queryEmail: Query = database?.orderByChild("staff_email")!!.equalTo(binding.edtEmail.text.toString())
        queryEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    showShortSnack(binding.root, "Email already exist")
                    hideLoader()
                } else {
                    val queryPassword: Query = database?.orderByChild("staff_pass")!!.equalTo(binding.edtPassword.text.toString())
                    queryPassword.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            hideLoader()
                            if (!snapshot.exists()) {
                                createStaffAccount()
                            }else{
                                showShortSnack(binding.root, "Password already exist")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            hideLoader()
                            showShortSnack(binding.root, error.message)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoader()
                showShortSnack(binding.root, error.message)
            }
        })
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
                showShortSnack(binding.root, "Account created successfully")
                getStaffData()
                hideLoader()

            }?.addOnFailureListener {
                showShortSnack(binding.root, it.message.toString())
                hideLoader()

            }

        binding.edtName.setText("")
        binding.edtEmail.setText("")
        binding.edtPassword.setText("")
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