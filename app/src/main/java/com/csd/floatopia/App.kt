package com.csd.floatopia

import android.app.Application
import com.csd.lib_framework.starter.dispatcher.TaskDispatcher
import com.csd.lib_framework.util.LogUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/21
 */
class App: Application() {
    companion object {
        private const val TAG = "App"
    }

    private val isDebug = true

    override fun onCreate() {
        super.onCreate()

        LogUtils.init(this, "Floatopia", isDebug)
        TaskDispatcher.init(this)

        TaskDispatcher.createInstance()
            .addTask(InitUtilsTask(this, isDebug))
            .addTask(InitKvManagerTask(this, mutableListOf()))
            .start()
            .await()

    }
}