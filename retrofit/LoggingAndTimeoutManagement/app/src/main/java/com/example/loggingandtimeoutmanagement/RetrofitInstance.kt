package com.example.loggingandtimeoutmanagement

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        val BASE_URL = "https://jsonplaceholder.typicode.com/"

        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY // 요청(Request) 및 응답(Response)의 모든 본문(Body) 내용을 로그로 기록하겠다는 설정
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // 30초간 서버와의 통신(연결상태 확인)을 시도, 30초 초과 시 통신 실패로 간주
                .readTimeout(20, TimeUnit.SECONDS) // 서버가 클라이언트에게 보내오는 데이터 패킷(단위) 간 도착 시간이 20초를 초과하면 통신 실패로 간주 (Response 받기를 포기)
                .writeTimeout(25, TimeUnit.SECONDS) // 클라이언트가 서버로 보내주는 패킷 간 도착 시간이 25초를 초과하면 통신 실패로 간주 (Response 받기를 포기)
        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}