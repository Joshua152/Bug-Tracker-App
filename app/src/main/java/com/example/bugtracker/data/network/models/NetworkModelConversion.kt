package com.example.bugtracker.data.network.models

/**
 * Interface for converting from the network model to the other models
 *
 * T - Network model
 * S - Local model
 * U - Domain model
 */
interface NetworkModelConversion<T, S, U> {
    fun asDatabaseModel(): S
    fun asDomainModel(): U
}

fun <T, S, U> List<NetworkModelConversion<T, S, U>>.asDatabaseModel(): List<S> {
    return map { it.asDatabaseModel() }
}

fun <T, S, U> List<NetworkModelConversion<T, S, U>>.asDomainModel(): List<U> {
    return map { it.asDomainModel() }
}