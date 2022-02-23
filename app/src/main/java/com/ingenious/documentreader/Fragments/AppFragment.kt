package com.ingenious.documentreader.Fragments;

import android.Manifest
import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ingenious.documentreader.Activities.AppActivity
import com.ingenious.documentreader.Helpers.AppPermissionManager
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.dialogs.AppDialog

public open class AppFragment : Fragment(){

    val permission_type_read_storage = Manifest.permission.READ_EXTERNAL_STORAGE
    val permission_type_manage_storage = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    fun checkForPermission(permissionType: String, permissionInterface: AppPermissionInterface) {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if(isGranted){
                permissionInterface.permissionGrated(permissionType)
            }else{
                permissionInterface.permissionDenied(permissionType)
            }
        }
        activity?.let {
            AppPermissionManager.checkPermission(
                permissionType,
                permissionInterface,
                it as AppActivity,
                permissionLauncher
            )
        }
    }

    fun showAppDialog(msg: String, title: String?, onOkClick: DialogInterface.OnClickListener?){
        val dialog = AppDialog(msg,title,onOkClick)
        activity?.let {
            dialog.show(it.supportFragmentManager,this.javaClass.simpleName)
        }
    }
}
