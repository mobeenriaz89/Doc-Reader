package com.ingenious.documentreader.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ingenious.documentreader.Models.File
import com.ingenious.documentreader.R

class FilesListAdapter(private val _filesList: ArrayList<File>, _context: Context?): RecyclerView.Adapter<FilesListAdapter.FilesViewHolder>() {

    private var filesList = _filesList
    private var context = _context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        var view = LayoutInflater.from(this.context).inflate(R.layout.viewholder_files_list,parent,false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.tvFileName.text = this.filesList[position].fileName
    }

    override fun getItemCount(): Int {
        return this.filesList.count()
    }

    class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
    }
}