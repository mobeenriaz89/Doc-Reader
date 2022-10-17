package com.ingenious.documentreader.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.R

class DocViewActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView
    private var filePath: String? = null
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_view)
        pdfView = findViewById(R.id.pdfView)
        loadBannerAd()
        when {
            intent?.action == Intent.ACTION_SEND || intent?.action == Intent.ACTION_VIEW -> {
                handleSharedPdf(intent)
            }
            else -> {
                intent.extras?.let {
                    filePath = it.getString(AppConstants.KEY_FILE_PATH)
                    var uri = Uri.parse(filePath)
                    loadPdfFile(uri)
                }
            }
        }

    }

    private fun loadBannerAd() {
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.docAdView)
        val adRequest = AdRequest.Builder()
        mAdView.loadAd(adRequest.build())
    }

    private fun handleSharedPdf(intent: Intent) {
        val sharedUri = intent.data
        sharedUri?.let{
            loadPdfFile(it)
        }
    }

    private fun loadPdfFile(fileUri: Uri){
        pdfView.fromUri(fileUri)
            .enableSwipe(true)
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this))
            .load()
    }

}