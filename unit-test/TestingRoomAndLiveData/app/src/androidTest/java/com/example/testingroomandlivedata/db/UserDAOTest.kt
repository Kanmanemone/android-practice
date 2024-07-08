package com.example.testingroomandlivedata.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.testingroom.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/* @RunWith 어노테이션이 없으면,
 * JUnit이 기본 테스트 러너(Test Runner)인 JUnit4 테스트 러너를 사용한다.
 * AndroidJUnit4 테스트 러너와는 달리, JUnit4 테스트 러너는 안드로이드 환경에 맞춰져 있지 않으므로,
 * 에러의 여지가 생겨버린다.
 */
@RunWith(AndroidJUnit4::class)
class UserDAOTest {

    /* InstantTaskExecutorRule 규칙(Rule)
     * 기본적으로 백그라운드 스레드에서 실행되는 LiveData를 메인 스레드에서 실행되도록 강제함.
     * LiveData가 백그라운드 스레드에서 그 값이 변경된다면, 메인 스레드에서 변화를 감지하지 못할 수도 있기 때문.
     * 또, 데이터의 변화를 감지하기 전에 테스트가 끝나버릴 여지도 있음.
     * 따라서, LiveData가 관여되는 테스트에는 InstantTaskExecutorRule 규칙이 많이 사용됨.
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: UserDAO
    private lateinit var database: UserDatabase

    @Before
    fun setUp() {
        // Dependency 초기화
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).build()

        // Dependent 초기화 (인터페이스 구현체를 인터페이스 프로퍼티에 넣는 것도 '의존성 주입'의 한 갈래다)
        dao = database.userDAO
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUsersTest() {
        runBlocking {
            // Insert
            val usersToInsert = listOf(
                User(1, "steve", "helloWorld@example.com"),
                User(2, "bob", "pizza1234@example.com"),
                User(3, "paul", "kenel@example.com"),
                User(4, "kevin", "chickenMania@example.com")
            )
            dao.insertUser2(usersToInsert)

            /* Get
             * 원래 LiveData의 value를 참조하려면, observe()를 통해,
             * LiveData의 값이 변화하는 순간을 포착해야한다.
             * 그 과정을 LiveDataTestUtil에서 알아서 진행해준다.
             * 만약 LiveDataTestUtil의 함수 없이,
             * val usersFetched = dao.getAllUsers().value와 같은 식의 코드였다면,
             * usersFetched에는 null이 담기게 된다.
             */
            val usersFetched = dao.getAllUsers().getOrAwaitValue()

            // 비교
            Truth.assertThat(usersFetched).isEqualTo(/* expected = */ usersToInsert)
        }
    }

    @Test
    fun deleteUsersTest() {
        runBlocking {
            // Insert
            val usersToInsert = listOf(
                User(1, "steve", "helloWorld@example.com"),
                User(2, "bob", "pizza1234@example.com"),
                User(3, "paul", "kenel@example.com"),
                User(4, "kevin", "chickenMania@example.com")
            )
            dao.insertUser2(usersToInsert)

            // Delete
            dao.deleteAll()

            // Get을 통한 확인
            val usersFetched = dao.getAllUsers().getOrAwaitValue()
            Truth.assertThat(usersFetched).isEmpty()
        }
    }
}