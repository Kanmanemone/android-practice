package com.example.automigrationspec.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [User::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = UserDatabase.Migration2To3::class)
    ]
)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDAO: UserDAO

    @RenameColumn(tableName = "user_data_table", fromColumnName = "user_age", toColumnName = "user_nai")
    class Migration2To3 : AutoMigrationSpec

    companion object {
        /* @Volatile 어노테이션은 클래스의 어떤 프로퍼티(필드)가 Update됐을 때,
         * 그 갱신된 값을 다른 스레드에서 바로 읽게(= 캐시가 아닌 메모리를 읽게) 한다.
         * 이 어노테이션이 없다면, 다른 스레드에서 갱신 이전의 값으로 잘못 읽을 수도 있다.
         * 시차가 존재하기 때문이다.
         */
        @Volatile
        private var INSTANCE: UserDatabase? = null
        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, UserDatabase::class.java, "user_data_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}