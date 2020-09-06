package com.gostev.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.hardware.Camera
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerFragment : Fragment(), ZXingScannerView.ResultHandler {

    private var mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private val EXTRA_BARCODE = "EXTRA_BARCODE"

    private lateinit var mCode: String

    private lateinit var mScannerView: ZXingScannerView
    private lateinit var mScannerText: TextView

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.scanner_fragment, container, false)

        mScannerView = view.findViewById(R.id.scanner)
        mScannerText = activity!!.findViewById(R.id.scanner_scan_text)
        mScannerText.visibility = View.VISIBLE
        mScannerText.isSelected

        return view
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onStop() {
        super.onStop()
        mScannerView.stopCamera()
    }

    private fun cameraStart() {
        mScannerView.setResultHandler(this)
        mScannerView.startCamera(mCameraId)
        mScannerView.setAutoFocus(true)
    }

    override fun handleResult(rawResult: Result?) {
        if (rawResult != null) {
            runAttention(R.raw.beep)
            runAttention(R.raw.select_variants)
            mScannerView.stopCamera()
            mCode = rawResult.text.trim { it <= ' ' }
            presentWebFragment()
        }
    }

    private fun checkPermission() {
        Dexter.withActivity(activity).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    cameraStart()
                }

                @SuppressLint("ShowToast")
                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(context, getString(R.string.no_permission_to_camera), Toast.LENGTH_LONG);
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?,
                    token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    @Synchronized
    private fun runAttention(@RawRes sound: Int) {
        var soundId = 0
        try {
            val music = SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0)
            music.setOnLoadCompleteListener { soundPool: SoundPool, sampleId: Int, status: Int ->
                soundPool.play(soundId, 1.0f, 1.0f, 100, 0, 1.0f)
            }
            soundId = music.load(context, sound, 1)
        } catch (e: Throwable) {
        }
    }

    private fun presentWebFragment() {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val webFragment = WebFragment()

        val args = Bundle()
        args.putString(EXTRA_BARCODE, mCode)
        webFragment.arguments = args

        fragmentTransaction.replace(R.id.frame, webFragment)
        fragmentTransaction.addToBackStack(webFragment.tag)
        fragmentTransaction.commit()
    }
}