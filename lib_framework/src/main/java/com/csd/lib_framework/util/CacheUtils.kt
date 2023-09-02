package com.csd.lib_framework.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/17
 */
object CacheUtils {


    /**
     * 获取应用总缓存大小
     *
     * @param context
     * @param dirPaths 额外的缓存目录
     * @return
     */
    fun getTotalCacheSize(context: Context, vararg dirPaths: String?): String {
        return try {
            var cacheSize: Long = 0 // = FileUtils.getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
            ) {
                val file = context.externalCacheDir
                cacheSize += if (file == null) 0 else FileUtils.getFileSize(file)
                for (dirPath in dirPaths) {
                    dirPath?.let {
                        val current = File(dirPath)
                        cacheSize += FileUtils.getFileSize(current)
                    }
                }
            }
            FileUtils.getFormatSizeString(cacheSize.toDouble())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            "0KB"
        }
    }

    /**
     * 获取缓存目录
     * @param context
     */
    fun getDiskCacheDir(context: Context): String? {
        val cachePath: String? =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }
        return cachePath
    }
}