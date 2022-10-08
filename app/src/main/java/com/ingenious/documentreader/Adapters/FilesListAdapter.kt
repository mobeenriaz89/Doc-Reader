package com.ingenious.documentreader.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Activities.DocViewActivity
import com.ingenious.documentreader.Helpers.AppConstants
import com.ingenious.documentreader.Models.FileModel
import com.ingenious.documentreader.R
import java.io.File

class FilesListAdapter(private val _filesList: ArrayList<FileModel>, _context: Context?): RecyclerView.Adapter<FilesListAdapter.FilesViewHolder>() {

    private var filesList = _filesList
    private var context = _context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        var view = LayoutInflater.from(this.context).inflate(R.layout.viewholder_files_list,parent,false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.tvFileName.text = this.filesList[position].fileName
        val thumbBmp = this.filesList[position].thumbUri
        thumbBmp?.let {
            holder.imgFileIcon.setImageBitmap(it)
        }
        holder.itemView.setOnClickListener{
           openDocActivity(this.filesList[position].filePath)
        }
    }

    override fun getItemCount(): Int {
        return this.filesList.count()
    }

    private fun openDocActivity(path: String){
        var intent = Intent(this.context, DocViewActivity::class.java)
        intent.putExtra(AppConstants.KEY_FILE_PATH,path)
        this.context?.startActivity(intent)
    }

    class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val imgFileIcon: ImageView = itemView.findViewById(R.id.imgFileIcon)
    }
}