package com.csd.floatopia

import android.app.Application
import com.csd.lib_framework.helper.AppHelper
import com.csd.lib_framework.manager.AppManager
import com.csd.lib_framework.manager.KvManager
import com.csd.lib_framework.starter.task.Task
import com.csd.lib_framework.util.SystemBarUtils
import com.csd.lib_framework.util.ToastUtils
import io.fastkv.interfaces.FastEncoder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */


class InitUtilsTask(
    private val application: Application,
    private val isDebug: Boolean

): Task() {
    override fun run() {
        AppHelper.init(application, isDebug)
        ToastUtils.init(application)
        SystemBarUtils.init(application)
        AppManager.init(application)
    }

}

/**
 * 初始化键值对
 * */
class InitKvManagerTask(
    private val application: Application,
    private val encoders: MutableList<FastEncoder<*>>
) : Task() {
    override fun run() {
        KvManager.init(application, encoders)
    }
}
