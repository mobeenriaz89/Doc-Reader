package com.ingenious.docreader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathUtils
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION_STORAGE = 1

    private lateinit var btnOpenFile: Button
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path: String? = uri?.path
        Toast.makeText(this,"File path: $path",Toast.LENGTH_LONG).show()

    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            btnOpenFile = findViewById(R.id.btnOpenFile)
            btnOpenFile.setOnClickListener { checkForPermission() }
        }


    private fun checkForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when{
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openDoc()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ->{
                    Toast.makeText(this,"Need storage permission",Toast.LENGTH_SHORT)
                }
                else -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ,REQUEST_CODE_PERMISSION_STORAGE)
                }
            }
        }
    }
    private fun openDoc() {
        val myIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        getContent.launch("application/pdf")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_PERMISSION_STORAGE -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    openDoc()
                }else{
                    Toast.makeText(this,"Permission not granted",Toast.LENGTH_SHORT)
                }
                return
            }
        }
    }


}