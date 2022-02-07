package com.ingenious.documentreader.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ingenious.documentreader.Fragments.ListFilesFragment
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Helpers.AppPermissionManager
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.R

class MainActivity : AppCompatActivity(), AppPermissionInterface {

    private lateinit var btnOpenFile: Button
    private lateinit var flContainer: FrameLayout

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openDocActivity(it.toString()) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpenFile = findViewById(R.id.btnOpenFile)
        flContainer = findViewById(R.id.flContainer)
        loadFilesFragment()
        btnOpenFile.setOnClickListener { AppPermissionManager.checkStoragePermission(this,this) }
    }

    private fun loadFilesFragment() {
        var ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.flContainer,ListFilesFragment())
        ft.commit()
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
        Toast.makeText(this,"Storage permission required",Toast.LENGTH_LONG).show()
    }

    override fun shouldShowRequestPermissionRationale() {
        Toast.makeText(this,"Storage permission required",Toast.LENGTH_LONG).show()
    }

}