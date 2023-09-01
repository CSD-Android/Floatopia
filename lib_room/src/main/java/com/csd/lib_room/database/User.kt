package com.csd.lib_room.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(var gameTimes: Int, var socialTimes: Int, var screenshotTimes: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 1
}