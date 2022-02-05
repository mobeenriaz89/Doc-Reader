package com.ingenious.documentreader.Helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ingenious.documentreader.Interfaces.AppPermissionInterface

object AppPermissionManager {
    fun checkStoragePermission(permissionManager: AppPermissionInterface, activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    permissionManager.permissionGrated()
                }
                activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                   permissionManager.shouldShowRequestPermissionRationale()
                }
                else -> {
                    activity.requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        AppConstants.REQUEST_CODE_PERMISSION_STORAGE
                    )
                }
            }
        }
    }
}