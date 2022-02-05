package com.ingenious.documentreader.Interfaces

interface AppPermissionInterface {
    fun permissionGrated()
    fun permissionDenied()
    fun shouldShowRequestPermissionRationale()
}