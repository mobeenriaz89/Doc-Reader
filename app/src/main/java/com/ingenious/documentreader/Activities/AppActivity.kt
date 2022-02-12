package com.ingenious.documentreader.Activities

import android.Manifest
import android.content.DialogInterface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ingenious.documentreader.Helpers.AppPermissionManager
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.dialogs.AppDialog

open class AppActivity: AppCompatActivity() {

    val permission_type_read_storage = Manifest.permission.READ_EXTERNAL_STORAGE
    val permission_type_manage_storage = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    fun checkForPermission(permissionType: String, permissionInterface: AppPermissionInterface) {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if(isGranted){
                permissionInterface.permissionGrated(permissionType)
            }else{
                permissionInterface.permissionDenied(permissionType)
            }
        }
        AppPermissionManager.checkPermission(permissionType,permissionInterface,this, permissionLauncher)
    }

    fun showAppDialog(msg: String, title: String?, onOkClick: DialogInterface.OnClickListener?){
        val dialog = AppDialog(msg,title,onOkClick)
        dialog.show(supportFragmentManager,this.javaClass.simpleName)
    }

}