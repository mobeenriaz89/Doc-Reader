package com.ingenious.documentreader.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Adapters.FilesListAdapter
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.Models.FileModel
import com.ingenious.documentreader.R
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFilesFragment : AppFragment(), AppPermissionInterface {

    private lateinit var rvFilesList: RecyclerView
    private lateinit var clNoAccess: ConstraintLayout
    private lateinit var btnRetry: Button

    private val filesList = ArrayList<FileModel>()

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if(Environment.isExternalStorageManager()) {
                    setupList()
                    clNoAccess.visibility = View.GONE
                }else
                    clNoAccess.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFilesList = view.findViewById(R.id.rvFilesList)
        clNoAccess = view.findViewById(R.id.clNoAccess)
        btnRetry = view.findViewById(R.id.btnRetry)

        btnRetry.setOnClickListener{ permissionCheck() }
        permissionCheck()
    }

    private fun permissionCheck(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activityResultLauncher.launch(intent)
            } else {
                setupList()
                clNoAccess.visibility = View.GONE
            }
        } else {
            checkForPermission(permission_type_manage_storage, this)
        }
    }

    private fun setupList() {
        rvFilesList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        activity?.let {
            searchDir(Environment.getExternalStorageDirectory())
            val filesListAdapter = FilesListAdapter(filesList, context)
            rvFilesList.adapter = filesListAdapter
        }
    }

    fun searchDir(dir: File) {
        val pdfPattern = ".pdf"
        val fileList = dir.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    searchDir(fileList[i])
                } else {
                    if (fileList[i].name.endsWith(pdfPattern)) {
                        filesList.add(
                            FileModel(
                                fileList[i].name,
                                fileList[i].toURI().toString(),
                                AppConstants.FILE_TYPE_APPLICATION_PDF
                            )
                        )
                    }
                }
            }
        }
    }

    override fun permissionGrated(type: String) {
        setupList()
        clNoAccess.visibility = View.GONE
    }

    override fun permissionDenied(type: String) {
        clNoAccess.visibility = View.VISIBLE
    }
}