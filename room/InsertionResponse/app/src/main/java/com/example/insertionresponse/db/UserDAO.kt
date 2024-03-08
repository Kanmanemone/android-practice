package com.example.insertionresponse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.insertionresponse.db.User

@Dao
interface UserDAO {

    @Insert
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 충돌 시 동작 정의 가능
    suspend fun insertUser2(user: User): Long

    @Insert
    suspend fun insertUser2(user: List<User>): List<Long> // 메소드 오버로딩

    @Insert
    suspend fun insertUser3(user: List<User>): Array<Long>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("INSERT INTO user_data_table (user_name, user_email) VALUES (:name, :email)")
    suspend fun insertUser4(name: String, email: String): Long

    @Query("DELETE FROM user_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_data_table")
    fun getAllUsers(): LiveData<List<User>>
}