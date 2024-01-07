package com.example.bugtracker.data.network.datasource

interface DataSource<T>{
    suspend fun get(id: Int): T?
    suspend fun getAll(): List<T>
    suspend fun add(vararg item: T): Boolean
    suspend fun add(items: List<T>): Boolean
    suspend fun update(id: Int, item: T): Boolean
    suspend fun delete(id: Int): Boolean
}