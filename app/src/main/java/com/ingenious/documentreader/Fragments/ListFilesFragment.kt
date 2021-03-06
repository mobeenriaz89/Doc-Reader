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
import android.widget.ProgressBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Adapters.FilesListAdapter
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Helpers.ItemDecoration
import com.ingenious.documentreader.Helpers.UIHelper
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.Models.FileModel
import com.ingenious.documentreader.R
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFilesFragment : AppFragment(), AppPermissionInterface, CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var rvFilesList: RecyclerView
    private lateinit var clNoAccess: ConstraintLayout
    private lateinit var btnRetry: Button
    private lateinit var progressbar: ProgressBar

    private var filesList = ArrayList<FileModel>()
    private lateinit var filesListAdapter: FilesListAdapter

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if(Environment.isExternalStorageManager()) {
                    loadList()
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
        progressbar = view.findViewById(R.id.progressbar)

        btnRetry.setOnClickListener{ permissionCheck() }
        setupList()
        permissionCheck()
    }

    private fun permissionCheck(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activityResultLauncher.launch(intent)
            } else {
                loadList()
                clNoAccess.visibility = View.GONE
            }
        } else {
            checkForPermission(permission_type_manage_storage, this)
        }
    }

    private fun setupList() {
            rvFilesList.layoutManager = GridLayoutManager(context,2)
        rvFilesList.addItemDecoration(
            ItemDecoration(UIHelper.dpToPx(8F,resources).roundToInt()))
                filesListAdapter = FilesListAdapter(filesList, context)
            rvFilesList.adapter = filesListAdapter
    }

    private fun loadList() {
        progressbar.visibility = View.VISIBLE
        launch {
            filesList =
                withContext(Dispatchers.IO) { searchDir(Environment.getExternalStorageDirectory()) }
            filesListAdapter.notifyDataSetChanged()
            progressbar.visibility = View.GONE
        }
    }

    fun searchDir(dir: File): ArrayList<FileModel> {
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
        return filesList
    }

    override fun permissionGrated(type: String) {
        loadList()
        clNoAccess.visibility = View.GONE
    }

    override fun permissionDenied(type: String) {
        clNoAccess.visibility = View.VISIBLE
        progressbar.visibility = View.GONE
    }
}