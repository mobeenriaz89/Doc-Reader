package com.ingenious.documentreader.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class AppDialog(var msg: String,
                var title: String?,
                var onOkClick: DialogInterface.OnClickListener?): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { act ->
            val builder = AlertDialog.Builder(act)
            builder.setMessage(msg)
                    onOkClick.let {
                        builder.setPositiveButton("ok",it)
                    }
            title?.let{
                builder.setTitle(it)
            }
            builder.create()
        } ?: throw IllegalStateException("Activty cannot be null")
    }
}