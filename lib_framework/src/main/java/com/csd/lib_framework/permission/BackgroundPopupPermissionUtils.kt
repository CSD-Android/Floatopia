package com.csd.lib_framework.permission

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.lang.reflect.Method

/**
 * 后台弹出界面的权限判断工具类
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/23
 */
object BackgroundPopupPermissionUtils {

    private const val TAG = "BackgroundPopupPermissionUtils"

    private fun checkManufacturer(manufacturer: String): Boolean {
        return manufacturer.equals(Build.MANUFACTURER, true)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun checkBgPopupPermissionAllowed(context: Context): Boolean {
        if (isXiaoMi()) {
            return isXiaomiBgPopupPermissionAllowed(context)
        }
        if (isVivo()) {
            return isVivoBgPopupPermissionAllowed(context)
        }
        if (isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    private fun isXiaoMi(): Boolean {
        return checkManufacturer("xiaomi")
    }

    private fun isOppo(): Boolean {
        return checkManufacturer("oppo")
    }

    private fun isVivo(): Boolean {
        return checkManufacturer("vivo")
    }

    private fun isXiaomiBgPopupPermissionAllowed(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val op = 10021
            val method: Method = ops.javaClass.getMethod("checkOpNoThrow", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
            val result = method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun isVivoBgPopupPermissionAllowed(context: Context): Boolean {
        return getVivoBgPopupPermissionStatus(context) == 0
    }

    /**
     * 判断Vivo后台弹出界面状态， 1无权限，0有权限
     * @param context context
     */
    @SuppressLint("Range")
    private fun getVivoBgPopupPermissionStatus(context: Context): Int {
        val uri: Uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(context.packageName)
        var state = 1
        try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
                if (it.moveToFirst()) {
                    state = it.getInt(it.getColumnIndex("currentstate"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return state
    }

}


