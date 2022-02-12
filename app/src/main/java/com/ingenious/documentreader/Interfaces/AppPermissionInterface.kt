package com.ingenious.documentreader.Interfaces

interface AppPermissionInterface {
    fun permissionGrated(type: String)
    fun permissionDenied(type: String)
}