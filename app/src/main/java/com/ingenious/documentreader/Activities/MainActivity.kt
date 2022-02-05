package com.ingenious.documentreader.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Helpers.AppPermissionManager
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.R

class MainActivity : AppCompatActivity(), AppPermissionInterface {

    private lateinit var btnOpenFile: Button

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openDocActivity(it.toString()) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenFile = findViewById(R.id.btnOpenFile)
        btnOpenFile.setOnClickListener { AppPermissionManager.checkStoragePermission(this,this) }
    }

    private fun openFileExplorer() {
        activityResultLauncher.launch(AppConstants.FILE_TYPE_APPLICATION_PDF)
    }

    private fun openDocActivity(path: String){
        var intent = Intent(this, DocViewActivity::class.java)
        intent.putExtra(AppConstants.KEY_FILE_PATH,path)
        startActivity(intent)
    }

    override fun permissionGrated() {
        openFileExplorer()
    }

    override fun permissionDenied() {
        Toast.makeText(this,"Storage permission required",Toast.LENGTH_LONG)
    }

    override fun shouldShowRequestPermissionRationale() {
        Toast.makeText(this,"Storage permission required",Toast.LENGTH_LONG)
    }

}