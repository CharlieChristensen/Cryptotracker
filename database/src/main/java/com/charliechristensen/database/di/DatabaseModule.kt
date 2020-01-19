package com.charliechristensen.database.di

import android.content.Context
import androidx.room.Room
import com.charliechristensen.database.AppDatabase
import com.charliechristensen.database.Database
import com.charliechristensen.database.DatabaseApi
import com.charliechristensen.database.DatabaseApiImpl
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    @Named("RoomDatabase")
    fun provideDatabaseApi(applicationContext: Context): DatabaseApi {
        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "coin-database")
            .fallbackToDestructiveMigration()
            .build()
        return DatabaseApiImpl(database)
    }

//    @Provides
//    @Singleton
//    @Named("SqlDelightDatabase")

    @Provides
    @Singleton
    fun providesSqlDelightDriver(applicationContext: Context): SqlDriver =
        AndroidSqliteDriver(Database.Schema, applicationContext, "cryptotracker-db.db")

    @Provides
    @Singleton
    fun providesSqlDelightDatabase(driver: SqlDriver): Database =
        Database(driver)

}
