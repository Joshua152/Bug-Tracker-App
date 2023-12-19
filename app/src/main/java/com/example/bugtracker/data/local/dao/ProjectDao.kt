package com.example.bugtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bugtracker.data.local.models.DatabaseBug
import com.example.bugtracker.data.local.models.DatabaseProject

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project ORDER BY project_id")
    fun getAll(): Array<DatabaseProject>

    @Query("SELECT * FROM project WHERE project_id = :projectID")
    fun getProject(projectID: Int): DatabaseProject

    @Query("SELECT * FROM bug WHERE project_id = :projectID ORDER BY project_id, bug_id")
    fun getBugs(projectID: Int): Array<DatabaseBug>

    @Insert
    fun add(projects: List<DatabaseProject>)

    @Query("DELETE FROM project")
    fun deleteAll()
}