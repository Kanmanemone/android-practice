package com.example.testingroomandlivedata.db

class UserRepository(private val dao: UserDAO) {

    val users = dao.getAllUsers()

    suspend fun insert(user: User) {
        dao.insertUser(user)
    }

    suspend fun update(user: User) {
        dao.updateUser(user)
    }

    suspend fun delete(user: User) {
        dao.deleteUser(user)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}