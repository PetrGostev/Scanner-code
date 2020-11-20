package com.gostev.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.gostev.scanner.databinding.ScannerFragmentBinding
import com.gostev.scanner.databinding.WebFragmentBinding

class WebFragment : Fragment(R.layout.web_fragment) {
    private val binding: WebFragmentBinding by viewBinding()

    private val EXTRA_BARCODE = "EXTRA_BARCODE"
    private lateinit var mCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = this.arguments
        if (args != null) {
            mCode = args.getString(EXTRA_BARCODE).toString()
        }
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.binding.scannerScanText.visibility = GONE
        binding.web.settings.javaScriptEnabled = true
        binding.web.loadUrl("https://www.google.com/search?q=" + mCode + "&gws_rd=ss")
    }
}