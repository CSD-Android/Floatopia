package com.csd.lib_framework.permission

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log


/**
 * 用于跳转应用的权限详情页
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/22
 */
object PermissionDetailUtils {
    private const val TAG = "PermissionRequest"

    /**
     * Build.MANUFACTURER
     */
    private const val MANUFACTURER_HUAWEI = "HUAWEI" //华为

    private const val MANUFACTURER_MEIZU = "Meizu" //魅族

    private const val MANUFACTURER_XIAOMI = "Xiaomi" //小米

    private const val MANUFACTURER_SONY = "Sony" //索尼

    private const val MANUFACTURER_OPPO = "OPPO" //oppo

    private const val MANUFACTURER_LG = "LG"

    private const val MANUFACTURER_VIVO = "vivo" //vivo

    private const val MANUFACTURER_SAMSUNG = "samsung" //三星

    private const val MANUFACTURER_ZTE = "ZTE" //中兴

    private const val MANUFACTURER_YULONG = "YuLong" //酷派

    private const val MANUFACTURER_LENOVO = "LENOVO" //联想


    /**
     * 跳转到应用详情权限页
     *
     * 需要注意使用 `try catch` 如果跳转失败则使用 [defaultIntent] 替代
     * @param context
     * */
    fun getIntent(context: Context): Intent {
        return when (Build.MANUFACTURER) {
            MANUFACTURER_HUAWEI -> Huawei(context)
            MANUFACTURER_MEIZU -> Meizu(context)
            MANUFACTURER_XIAOMI -> Xiaomi(context)
            MANUFACTURER_SONY -> Sony(context)
            MANUFACTURER_OPPO -> OPPO(context)
            MANUFACTURER_VIVO -> VIVO(context)
            MANUFACTURER_LG -> LG(context)
            else -> {
                Log.e(TAG, "目前暂不支持此系统")
                defaultIntent(context)
            }
        }
    }

    private fun Huawei(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", context.applicationInfo.packageName)
        val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        intent.component = comp
        return intent
    }

    private fun Meizu(context: Context): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("packageName", context.packageName)
        return intent
    }

    private fun Xiaomi(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.packageName)
        val componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        intent.component = componentName
        return intent
    }

    private fun Sony(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", context.packageName)
        val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
        intent.component = comp
        return intent
    }

    private fun OPPO(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", context.packageName)
        val comp = ComponentName(
            "com.coloros.securitypermission",
            "com.coloros.securitypermission.permission.PermissionAppAllPermissionActivity"
        ) // R11t 7.1.1 os-v3.2
        intent.component = comp
        return intent
    }

    private fun VIVO(context: Context): Intent {
        val localIntent: Intent
        if (Build.MODEL.contains("Y85") && !Build.MODEL.contains("Y85A") || Build.MODEL.contains("vivo Y53L")) {
            localIntent = Intent()
            localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity")
            localIntent.putExtra("packagename", context.packageName)
            localIntent.putExtra("tabId", "1")
        } else {
            localIntent = Intent()
            localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity")
            localIntent.action = "secure.intent.action.softPermissionDetail"
            localIntent.putExtra("packagename", context.packageName)
        }
        return localIntent
    }

    private fun LG(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.putExtra("packageName", context.packageName)
        val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
        intent.component = comp
        context.startActivity(intent)
        return intent
    }

    /**
     * 只能打开到自带安全软件
     * @param context
     */
    private fun _360(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", context.packageName)
        val comp = ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity")
        intent.component = comp
        return intent
    }

    /**
     * 应用信息界面
     * @param context
     */
    @SuppressLint("ObsoleteSdkInt")
    fun applicationInfo(context: Context): Intent {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", context.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
        }
        return localIntent
    }

    /**
     * 系统设置界面
     */
    fun systemConfig(): Intent {
        return Intent(Settings.ACTION_SETTINGS)
    }

    /**
     * 默认打开应用详细页
     */
    private fun defaultIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    }

}