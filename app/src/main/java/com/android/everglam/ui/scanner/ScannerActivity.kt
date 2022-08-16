package com.android.everglam.ui.scanner

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.everglam.R
import com.android.everglam.databinding.ActivityScannerBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private val FLASH_STATE = "FLASH_STATE"
    private val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
    private val SELECTED_FORMATS = "SELECTED_FORMATS"
    private val CAMERA_ID = "CAMERA_ID"
    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    private val binding: ActivityScannerBinding by lazy {
        ActivityScannerBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false)
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = state.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }

        setContentView(binding.root)

        mScannerView = ZXingScannerView(this)
        setupFormats()
        binding.contentFrame.addView(mScannerView)
    }


    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera(mCameraId)
        mScannerView!!.flash = mFlash
        mScannerView!!.setAutoFocus(mAutoFocus)
    }

    fun setupFormats() {
        val formats: MutableList<BarcodeFormat> = ArrayList()
        if (mSelectedIndices == null || mSelectedIndices!!.isEmpty()) {
            mSelectedIndices = ArrayList()
            for (i in ZXingScannerView.ALL_FORMATS.indices) {
                mSelectedIndices!!.add(i)
            }
        }
        for (index in mSelectedIndices!!) {
            formats.add(ZXingScannerView.ALL_FORMATS[index])
        }
        if (mScannerView != null) {
            mScannerView!!.setFormats(formats)
        }
    }

    override fun handleResult(result: Result?) {
        Toast.makeText(this@ScannerActivity, "Result: ".plus(result!!.text.toString()), Toast.LENGTH_SHORT).show()

        val handler = Handler()
        handler.postDelayed(
            { mScannerView!!.resumeCameraPreview(this@ScannerActivity) },
            2000
        )

    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    fun toggleFlash(v: View?) {
        mFlash = !mFlash
        mScannerView!!.flash = mFlash

        if (mScannerView!!.flash){
            binding.imgFlash.setImageDrawable(ContextCompat.getDrawable(this@ScannerActivity, R.drawable.ic_flash_on))
        }else{
            binding.imgFlash.setImageDrawable(ContextCompat.getDrawable(this@ScannerActivity, R.drawable.ic_flash_off))
        }


    }
}