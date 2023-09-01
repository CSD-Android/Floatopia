package com.csd.lib_room.manager

import com.csd.lib_room.database.User
import com.csd.lib_room.database.UserDatabase


object UserManager {
    private val dao by lazy { UserDatabase.getDataBase() }

    fun getUser() : User {
        return dao.userDao().loadAllUsers()
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

    fun deleteAll(){
        dao.userDao().deleteAll()
    }

    fun insertUser(user: User){
        dao.userDao().insertUser(user)
    }


}