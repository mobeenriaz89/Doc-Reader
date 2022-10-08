package com.ingenious.documentreader.Helpers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.ingenious.documentreader.Activities.AppActivity
import com.ingenious.documentreader.Interfaces.AppPermissionInterface
import com.ingenious.documentreader.dialogs.AppDialog

object AppPermissionManager {
    fun checkPermission(
        type: String,
        permissionManager: AppPermissionInterface,
        activity: AppActivity,
        permissionLauncher: ActivityResultLauncher<String>
    ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    type
                ) == PackageManager.PERMISSION_GRANTED -> {
                    permissionManager.permissionGranted(type)
                }
                activity.shouldShowRequestPermissionRationale(type) -> {
                showPermissionReasonDialog(type,permissionLauncher,activity)
                }
                else -> {
                    permissionLauncher.launch(type)
                }
            }
        }
    }

    fun showPermissionReasonDialog(
        type: String,
        permissionLauncher: ActivityResultLauncher<String>,
        activity: AppActivity
    ){
        var msg = ""
        when(type){
            Manifest.permission.READ_EXTERNAL_STORAGE -> msg = "This feature requires storage permission"
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> msg = "This feature requires files access permission"
        }
        val dialog = AppDialog(msg,null) { _, _ ->
            permissionLauncher.launch(type)
        }
        dialog.show(activity.supportFragmentManager,activity.javaClass.simpleName)
    }
}