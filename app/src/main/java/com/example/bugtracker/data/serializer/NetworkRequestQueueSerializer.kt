package com.example.bugtracker.data.serializer

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.bugtracker.NetworkRequestQueue
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object NetworkRequestQueueSerializer : Serializer<NetworkRequestQueue> {
    override val defaultValue = NetworkRequestQueue.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NetworkRequestQueue {
        try {
            return NetworkRequestQueue.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: NetworkRequestQueue, output: OutputStream) = t.writeTo(output)
}

// look into Multi process data store when upgrade to version 1.1.0
val Context.networkRequestQueueStore: DataStore<NetworkRequestQueue> by dataStore(
    fileName = "network_request.pb",
    serializer = NetworkRequestQueueSerializer
)