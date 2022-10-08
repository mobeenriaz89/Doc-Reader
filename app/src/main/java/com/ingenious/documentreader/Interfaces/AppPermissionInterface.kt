package com.ingenious.documentreader.Interfaces

interface AppPermissionInterface {
    fun permissionGranted(type: String)
    fun permissionDenied(type: String)
}