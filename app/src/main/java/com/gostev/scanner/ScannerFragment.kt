package com.gostev.scanner

import android.Manifest
import android.hardware.Camera
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.zxing.Result
import com.gostev.scanner.databinding.ScannerFragmentBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerFragment : Fragment(R.layout.scanner_fragment), ZXingScannerView.ResultHandler {

    private var mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private val EXTRA_BARCODE = "EXTRA_BARCODE"

    private lateinit var mCode: String
    private val binding: ScannerFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.binding.scannerScanText.visibility = View.VISIBLE
        MainActivity.binding.scannerScanText.isSelected
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onStop() {
        super.onStop()
        binding.scanner.stopCamera()
    }

    private fun cameraStart() {
        binding.scanner.setResultHandler(this)
        binding.scanner.startCamera(mCameraId)
        binding.scanner.setAutoFocus(true)
    }

    override fun handleResult(rawResult: Result?) {
        if (rawResult != null) {
            runAttention(R.raw.beep)
            runAttention(R.raw.select_variants)
            binding.scanner.stopCamera()
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

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(context, getString(R.string.no_permission_to_camera), Toast.LENGTH_LONG).show();
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
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
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