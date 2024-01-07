package com.example.bugtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bugtracker.data.local.models.DatabaseBug

@Dao
interface BugDao {
    @Query("SELECT * FROM bug ORDER BY project_id, bug_id")
    fun getAll(): List<DatabaseBug>

    @Query("SELECT * FROM bug WHERE bug.bug_id = :id")
    fun get(id: Int): DatabaseBug?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(vararg bugs: DatabaseBug)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(bugs: List<DatabaseBug>)

    @Query("DELETE FROM bug WHERE bug.bug_id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM bug")
    fun deleteAll()
}