package com.csd.lib_room.manager

import cn.csd.lib_room.database.UserDatabase


object UserManager {
    private val dao by lazy { UserDatabase.getDataBase() }

    fun getUser() {
        dao.userDao().loadAllUsers()
    }

    fun updateGameTimes(){
        dao.userDao().incrementGameTimes()
    }

    fun updateSocialTimes(){
        dao.userDao().incrementSocialTimes()
    }

    fun updateShotTimes(){
        dao.userDao().incrementScreenshotTimes()
    }


}