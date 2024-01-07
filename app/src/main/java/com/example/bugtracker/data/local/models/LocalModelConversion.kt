package com.example.bugtracker.data.local.models

/**
 * Interface for converting from the local model to the other models
 *
 * T - Local model
 * S - Network model
 * U - Domain model
 */
interface LocalModelConversion<T, S, U> {
    fun asNetworkModel(): S
    fun asDomainModel(): U
}

fun <T, S, U> List<LocalModelConversion<T, S, U>>.asNetworkModel(): List<S> {
    return map { it.asNetworkModel() }
}

fun <T, S, U> List<LocalModelConversion<T, S, U>>.asDomainModel(): List<U> {
    return map { it.asDomainModel() }
}
