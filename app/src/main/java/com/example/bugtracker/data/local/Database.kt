package com.example.bugtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bugtracker.data.local.dao.BugDao
import com.example.bugtracker.data.local.dao.ProjectDao
import com.example.bugtracker.data.local.models.DatabaseBug
import com.example.bugtracker.data.local.models.DatabaseProject

@Database(entities = [DatabaseProject::class, DatabaseBug::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun bugDao(): BugDao
}