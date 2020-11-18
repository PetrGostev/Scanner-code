package com.gostev.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gostev.scanner.databinding.WebFragmentBinding

class WebFragment : Fragment() {

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
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = WebFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        MainActivity.binding.scannerScanText.visibility = GONE
        binding.web.settings.javaScriptEnabled = true
        binding.web.loadUrl("https://www.google.com/search?q=" + mCode + "&gws_rd=ss")

        return view
    }
}