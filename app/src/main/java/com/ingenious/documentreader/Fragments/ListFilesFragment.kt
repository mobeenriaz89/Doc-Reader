package com.ingenious.documentreader.Fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Adapters.FilesListAdapter
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Models.FileModel
import com.ingenious.documentreader.R
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFilesFragment : Fragment() {

    lateinit var filesList: ArrayList<FileModel>

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvFilesList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFilesList = view.findViewById(R.id.rvFilesList)
        setupList()
    }

    private fun setupList() {
        filesList = ArrayList()
        rvFilesList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        searchDir(Environment.getExternalStorageDirectory())//dummyList()
        val filesListAdapter = FilesListAdapter(filesList, context)
        rvFilesList.adapter = filesListAdapter


    }

    private fun dummyList(): ArrayList<FileModel> {
        val list = ArrayList<FileModel>()
        for (i in 0..5) {
            list.add(FileModel("file_$i.pdf", "somewhere", AppConstants.FILE_TYPE_APPLICATION_PDF))
        }
        return list
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFilesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFilesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun searchDir(dir: File) {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    searchDir(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        filesList.add(
                            FileModel(
                                FileList[i].name,
                                FileList[i].toURI().toString(),
                                AppConstants.FILE_TYPE_APPLICATION_PDF
                            )
                        )
                    }
                }
            }
        }
    }
}