package com.example.livedatabuilder.model

import kotlinx.coroutines.delay

class UserRepository {

    suspend fun getUsers(): List<User> {
        delay(8000)
        val users: List<User> = listOf(
            User(1, "Park Bom"),
            User(2, "Park Sandara"),
            User(3, "CL"),
            User(4, "Gong Minji")
        )
        return users
    }
}