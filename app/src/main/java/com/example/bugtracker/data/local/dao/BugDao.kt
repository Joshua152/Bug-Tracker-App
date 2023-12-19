package com.example.bugtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bugtracker.data.local.models.DatabaseBug

@Dao
interface BugDao {
    @Query("SELECT * FROM bug ORDER BY project_id, bug_id")
    fun getAll(): List<DatabaseBug>

    @Query("SELECT * FROM bug WHERE bug.bug_id = :bugID")
    fun getBug(bugID: Int): DatabaseBug

    @Insert
    fun add(bugs: List<DatabaseBug>)

    @Query("DELETE FROM bug")
    fun deleteAll()
}