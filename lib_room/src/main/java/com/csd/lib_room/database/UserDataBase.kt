package cn.csd.lib_room.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room

import androidx.room.RoomDatabase
import com.csd.lib_framework.helper.AppHelper

@Database(version = 1, entities = [User::class])
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: UserDatabase? = null

        @Synchronized
        fun getDataBase(): UserDatabase {
            return instance?: Room.databaseBuilder(context = AppHelper.application,UserDatabase::class.java,"Times_Database")
                // 是否允许在主线程查询，默认是false
                .allowMainThreadQueries()
                // 数据库被创建或者被打开时的回调
                // .addCallback(callBack)
                // 指定数据查询的线程池，不指定会有个默认的
                // .setQueryExecutor {  }
                // 任何数据库有变更版本都需要升级，升级的同时需要指定migration，如果不指定则会报错
                // 数据库升级 1-->2， 怎么升级，以什么规则升级
                .addMigrations()
                // 设置数据库工厂，用来链接room和SQLite，可以利用自行创建SupportSQLiteOpenHelper，来实现数据库加密
                // .openHelperFactory()
                .build()
        }
    }
}