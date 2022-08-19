package com.android.everglam.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.android.everglam.R
import com.android.everglam.databinding.ActivityScannerBinding
import com.android.everglam.ui.base.BaseActivity
import com.android.everglam.ui.productdetail.ScanedProductDetailsActivity
import com.android.everglam.utils.goToWithBundle
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScannerActivity : BaseActivity() {

    private val FLASH_STATE = "FLASH_STATE"
    private val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
    private val SELECTED_FORMATS = "SELECTED_FORMATS"
    private val CAMERA_ID = "CAMERA_ID"
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var flag = false
    private var mCameraId = -1
    private val binding: ActivityScannerBinding by lazy {
        ActivityScannerBinding.inflate(
            layoutInflater
        )
    }
    private var beep: MediaPlayer? = null
    private var isBeepPlay = false
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    private var barcode: Barcode? = null

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

        //media
        beep = MediaPlayer.create(this, R.raw.beep)
        initViews()
    }

    private fun initViews() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

         }

    override fun onResume() {
        super.onResume()
        flag = false
        initialiseDetectorsAndSources()
    }



    override fun onPause() {
        super.onPause()
        //mScannerView!!.stopCamera()
    }

    private fun initialiseDetectorsAndSources() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                createSurface()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })

        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode?> {
            override fun release() {}
            override fun receiveDetections(detections: Detector.Detections<Barcode?>) {
               val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    if (!isBeepPlay){
                        beep?.start()
                        isBeepPlay = true
                    }


                    barcode = barcodes.valueAt(0)
                    val result = barcode?.rawValue
                    if (!flag) {
                        goToWithBundle(ScanedProductDetailsActivity::class.java){
                            putString("Result", result.toString())
                        }
                        beep?.start()
                    }
                    flag = true

                }
            }
        })
    }

    fun createSurface(){
        try {
            if (ActivityCompat.checkSelfPermission(this@ScannerActivity, Manifest.permission.CAMERA) === PackageManager.PERMISSION_GRANTED) {
                cameraSource?.start(binding.surfaceView.holder)
            } else {
                ActivityCompat.requestPermissions(this@ScannerActivity, arrayOf(Manifest.permission.CAMERA), 201)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}