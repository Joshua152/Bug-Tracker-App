package com.example.bugtracker.data.domain

/**
 * Interface for converting from the domain model to the other models
 *
 * T - Domain model
 * S - Local model
 * U - Network model
 */
interface DomainModelConversions<T, S, U> {
    fun asDatabaseModel(): S
    fun asNetworkModel(): U
}

fun <T, S, U> List<DomainModelConversions<T, S, U>>.asDatabaseModel(): List<S> {
    return map { it.asDatabaseModel() }
}

fun <T, S, U> List<DomainModelConversions<T, S, U>>.asNetworkModel(): List<U> {
    return map { it.asNetworkModel() }
}
