package com.gostev.scanner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment

class WebFragment : Fragment() {

    private val EXTRA_BARCODE = "EXTRA_BARCODE"
    private lateinit var mCode: String

    private lateinit var mWebView: WebView
    private lateinit var mScannerText: TextView

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

        val view = inflater.inflate(R.layout.web_fragment, container, false)

        mWebView = view.findViewById(R.id.web)
        mScannerText = activity!!.findViewById(R.id.scanner_scan_text)

        mScannerText.visibility = GONE
        mWebView.settings.javaScriptEnabled = true
        mWebView.loadUrl("https://www.google.com/search?q=" + mCode + "&gws_rd=ss")

        return view
    }
}