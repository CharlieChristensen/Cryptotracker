package com.charliechristensen.database.di

import android.content.Context
import androidx.room.Room
import com.charliechristensen.database.AppDatabase
import com.charliechristensen.database.DatabaseApi
import com.charliechristensen.database.DatabaseApiImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseApi(applicationContext: Context): DatabaseApi {
        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "coin-database")
            .fallbackToDestructiveMigration()
            .build()
        return DatabaseApiImpl(database)
    }
}
