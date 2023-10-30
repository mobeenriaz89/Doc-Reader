package com.ingenious.documentreader.Activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ingenious.documentreader.Fragments.ListFilesFragment
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Helpers.AppPermissionManager
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.R


class MainActivity : AppActivity(), AppPermissionInterface {

    private lateinit var btnOpenFile: Button
    private lateinit var flContainer: FrameLayout

    lateinit var mAdView : AdView

    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted){
            permissionGranted("")
        }else{
            permissionDenied("permissionType")
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openDocActivity(it.toString()) }
        }

    private var getFilesLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            openDocActivity(data.data.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenFile = findViewById(R.id.btnOpenFile)
        flContainer = findViewById(R.id.flContainer)
        btnOpenFile.setOnClickListener {
            //checkForPermission(permission_type_read_storage,this)
            AppPermissionManager.checkPermission(permission_type_read_storage,this,this,permissionLauncher)
        }
            loadFilesFragment()
            loadBannerAd()
        }

    private fun loadBannerAd() {
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder()
        mAdView.loadAd(adRequest.build())
    }


    private fun loadFilesFragment() {
        var ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.flContainer,ListFilesFragment())
        ft.commit()
    }

    private fun openFileExplorer() {
       // activityResultLauncher.launch(AppConstants.FILE_TYPE_APPLICATION_PDF)
        val pdfIntent = Intent(ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        getFilesLauncher.launch(pdfIntent)
    }

    private fun openDocActivity(path: String){
        var intent = Intent(this, DocViewActivity::class.java)
        intent.putExtra(AppConstants.KEY_FILE_PATH,path)
        startActivity(intent)
    }

    override fun permissionGranted(type: String) {
        openFileExplorer()
    }

    override fun permissionDenied(type: String) {
        showAppDialog("App failed to gain storage access",null,null)
    }

}