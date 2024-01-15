package com.example.bugtracker.networkutils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.bugtracker.data.network.datasource.BugDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class InternetConnectivityObserver(
    private val context: Context
) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: InternetConnectivityObserver? = null

        /**
         * Gets singleton instance of the InternetConnectivityObserver
         * @param context The context
         */
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: InternetConnectivityObserver(context).also { instance = it }
            }
    }

    private val onGainObservers: MutableList<() -> Unit> = mutableListOf()

    private var lastAvailability: Boolean = false
    private var listening: Boolean = false

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            if (!lastAvailability) {
                onGainObservers.forEach {
                    it.invoke()
                }
            }

            lastAvailability = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            lastAvailability = false
        }
    }

    fun listenOnGain(callback: () -> Unit) {
        onGainObservers.add(callback)

        if (!listening) {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, networkCallback)

            listening = true
        }
    }

    fun removeAllObservers() {
        onGainObservers.clear()
    }

    // TRY LATER TO MAKE IT WORK WITH PING
    fun ping(): Boolean {
        return try {
            val timeoutMs = 1500
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            socket.connect(socketAddress, timeoutMs)
            socket.close()

            true
        } catch(e: IOException) {
            false
        }
    }
}