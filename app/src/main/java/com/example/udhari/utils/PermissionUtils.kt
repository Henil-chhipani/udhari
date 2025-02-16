package com.example.udhari.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun checkArrayOfPermission(context: Context, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun checkIfPermissionGiven(permissions:  Map<String, @JvmSuppressWildcards Boolean>, permissionsNeeded: List<String>): Boolean {
        for (permission in permissionsNeeded) {
            if (permissions[permission] == false) return false
        }
        return true
    }
}