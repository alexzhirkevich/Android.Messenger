package com.alexz.messenger.app.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionChecker(private var context: Context){

    /**
     * @param permission to check
     * @return [PermissionRequester] for [permission] or null, if [permission] is granted
     * */
    fun check(permission: String) : PermissionRequester? =
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            null
        else PermissionRequester(arrayOf(permission))

    /**
     * @param permissions to check
     * @return [PermissionRequester] for non-granted [permissions] or null, if all [permissions] are granted
     * */
    fun check(permissions: Array<String>) : PermissionRequester? =
        permissions.filter { check(it) != null }.let {
            if (it.isEmpty()) null else PermissionRequester(it.toTypedArray())
        }

    class PermissionRequester(private val permissions:Array<String>) {

        /**
         * Requests permissions
         *
         * @param activity for callback
         * @param requestCode for callback
         * */
        fun request(activity: Activity, requestCode: Int) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }
}