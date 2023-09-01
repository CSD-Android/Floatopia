package com.csd.lib_room.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.csd.lib_room.database.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User): Long

    //打开游戏次数加一
    @Query("UPDATE User SET gameTimes = gameTimes + 1")
    fun incrementGameTimes()

    //打开社交软件次数加一
    @Query("UPDATE User SET socialTimes = socialTimes + 1")
    fun incrementSocialTimes()

    //截屏次数加一
    @Query("UPDATE User SET screenshotTimes = screenshotTimes + 1")
    fun incrementScreenshotTimes()

    @Query("DELETE FROM USER")
    fun deleteAll()

    @Query("select * from User")
    fun loadAllUsers(): User

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM User WHERE gameTimes > 100")
    fun accomplishGameTimes(): User

    @Query("SELECT * FROM User WHERE socialTimes > 100")
    fun accomplishSocialTimes(): User

    @Query("SELECT * FROM User WHERE screenshotTimes > 100")
    fun accomplishScreenshotTimes(): User

//    @Query("delete from User where lastName = :lastName")
//    fun deleteUserByLastName(lastName: String): Int

}