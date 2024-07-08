package com.example.testingretrofit

import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumServiceTest {

    private lateinit var service: AlbumService
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        // Dependency (Test double) 초기화
        server = MockWebServer()

        // Dependent 초기화
        service = Retrofit.Builder()
            .baseUrl(server.url("")) // baseUrl (Mock이라서 baseUrl을 굳이 제대로 넣을 필요 없다. 뭘 넣어도 하드 코딩된 response를 내뱉을 것이다.)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlbumService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    /* Server에 하드코딩된 JSON 파일 넣기.
     * server.enqueue(mockResponse)된 순간부터,
     * Server에 무슨 요청을 하든 mockResponse를 응답해줌.
     * 물론 Queue이므로 First In First Out이고,
     * n개의 응답이 필요하다면 server.enqueue()를 n번해주어야 한다.
     */
    private fun enqueueMockResponse(fileName: String) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setBody(source.readString(Charsets.UTF_8))
        server.enqueue(mockResponse)
    }

    // 올바른 request를 보내는 지, 응답이 오긴하는 지 확인
    @Test
    fun getAlbums_sentRequest_receivedExpected() { // subjectUnderTest_actionOrInput_resultState 규칙으로 함수 명명함
        runBlocking {
            // Mock 서버에 하드코딩된 데이터 넣고, 곧바로 request 및 response 받기
            enqueueMockResponse("album_response.json")
            val responseBody = service.getAlbums().body()

            /* 서버가 받은 HTTP request 가져오고, 올바른 request였는지 확인.
             * @Query 어노테이션이 존재한다면,
             * 예를 들어 "/albums?userId=7"와 같이 적어야 한다.
             */
            val request = server.takeRequest()
            Truth.assertThat(request.path).isEqualTo(/* expected = */ "/albums")

            // response이 null이 아님을 확인 (Mock 서버가 뭐라도 잘 내뱉는 지 확인)
            Truth.assertThat(responseBody).isNotNull()
        }
    }

    // Albums의 갯수 확인
    @Test
    fun getAlbums_receivedResponse_correctElementSize() {
        runBlocking {
            // Mock 서버에 하드코딩된 데이터 넣고, 곧바로 request 및 response 받기
            enqueueMockResponse("album_response.json")
            val responseBody = service.getAlbums().body()

            Truth.assertThat(responseBody!!.size).isEqualTo(/* expected = */ 100)
        }
    }

    // 모든 Albums를 다 확인하는 건 시간도 오래 걸리고 필요성도 크지 않으니, 첫번째 앨범만 확인
    @Test
    fun getAlbums_receivedResponse_correctContent() {
        runBlocking {
            // Mock 서버에 하드코딩된 데이터 넣고, 곧바로 request 및 response 받기
            enqueueMockResponse("album_response.json")
            val responseBody = service.getAlbums().body()
            val firstAlbum = responseBody!![0]

            Truth.assertThat(firstAlbum.userId).isEqualTo(/* expected = */ 1)
            Truth.assertThat(firstAlbum.id).isEqualTo(/* expected = */ 1)
            Truth.assertThat(firstAlbum.title).isEqualTo(/* expected = */ "quidem molestiae enim")
        }
    }
}