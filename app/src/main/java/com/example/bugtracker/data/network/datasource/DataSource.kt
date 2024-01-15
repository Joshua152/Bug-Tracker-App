package com.example.bugtracker.data.network.datasource

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.NetworkRequest
import com.example.bugtracker.data.serializer.networkRequestQueueStore
import com.example.bugtracker.networkutils.InternetConnectivityObserver
import java.util.LinkedList
import java.util.Queue

abstract class DataSource<T> {
    abstract suspend fun get(id: Int): T?
    abstract suspend fun getAll(): List<T>
    abstract suspend fun add(vararg item: T): Boolean
    abstract suspend fun add(items: List<T>): Boolean
    abstract suspend fun update(id: Int, item: T): Boolean
    abstract suspend fun delete(id: Int): Boolean
}