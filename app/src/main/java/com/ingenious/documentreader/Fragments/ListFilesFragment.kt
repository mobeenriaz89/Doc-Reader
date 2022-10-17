package com.ingenious.documentreader.Fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Adapters.FilesListAdapter
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Helpers.ItemDecoration
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.Models.FileModel
import com.ingenious.documentreader.R
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext

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
                    clNoAccess.visibility = View.GONE
                    loadList()
                }else
                    clNoAccess.visibility = View.VISIBLE
            }
        }

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted){
            permissionGranted(permission_type_manage_storage)
        }else{
            permissionDenied(permission_type_manage_storage)
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

        btnRetry.setOnClickListener{ permissionCheck(true) }
        setupList()
        permissionCheck(false)
    }

    private fun permissionCheck(didRetry: Boolean){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                if(didRetry) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    activityResultLauncher.launch(intent)
                }else{
                    clNoAccess.visibility = View.VISIBLE
                }
            } else {
                loadList()
                clNoAccess.visibility = View.GONE
            }
        } else {
            checkForPermission(permissionLauncher,permission_type_manage_storage, this)
        }
    }

    private fun setupList() {
            rvFilesList.layoutManager = GridLayoutManager(context,2)
        rvFilesList.addItemDecoration(
            ItemDecoration(20))
                filesListAdapter = FilesListAdapter(filesList, context)
            rvFilesList.adapter = filesListAdapter
    }

    private fun loadList() {
        progressbar.visibility = View.VISIBLE
        launch {
            filesList =
                withContext(Dispatchers.IO) { searchDir(Environment.getExternalStorageDirectory()) }
            filesListAdapter.notifyDataSetChanged()
            clNoAccess.visibility = View.GONE
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
                        generateImageFromPdf(Uri.fromFile(fileList[i]))
                        filesList.add(
                            FileModel(
                                fileList[i].name,
                                fileList[i].toURI().toString(),
                                AppConstants.FILE_TYPE_APPLICATION_PDF,
                                generateImageFromPdf(Uri.fromFile(fileList[i]))
                            )
                        )
                    }
                }
            }
        }
        return filesList
    }

    override fun permissionGranted(type: String) {
        loadList()
        clNoAccess.visibility = View.GONE
    }

    override fun permissionDenied(type: String) {
        clNoAccess.visibility = View.VISIBLE
        progressbar.visibility = View.GONE
    }

    fun generateImageFromPdf(pdfUri: Uri?): Bitmap? {
        val pageNumber = 0
        val pdfiumCore = PdfiumCore(context)
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            val fd: ParcelFileDescriptor? =
                pdfUri?.let { activity?.contentResolver?.openFileDescriptor(it, "r") }
            val pdfDocument: PdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            saveImage(bmp)
            pdfiumCore.closeDocument(pdfDocument)
        return bmp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    val FOLDER = Environment.getExternalStorageDirectory().toString() + "/PDF"
    private fun saveImage(bmp: Bitmap) {
        var out: FileOutputStream? = null
        try {
            val folder = File(FOLDER)
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, "PDF.png")
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
        } catch (e: Exception) {
            //todo with exception
        } finally {
            try {
                if (out != null) out.close()
            } catch (e: Exception) {
                //todo with exception
            }
        }
    }
}