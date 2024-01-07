package com.example.bugtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bugtracker.data.local.models.DatabaseBug
import com.example.bugtracker.data.local.models.DatabaseProject

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project ORDER BY project_id")
    fun getAll(): List<DatabaseProject>

    @Query("SELECT * FROM project WHERE project_id = :id")
    fun get(id: Int): DatabaseProject?

    @Query("SELECT * FROM bug WHERE project_id = :projectID ORDER BY project_id, bug_id")
    fun getBugs(projectID: Int): List<DatabaseBug>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg projects: DatabaseProject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(projects: List<DatabaseProject>)

    @Query("DELETE FROM project WHERE project_id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM project")
    fun deleteAll()
}