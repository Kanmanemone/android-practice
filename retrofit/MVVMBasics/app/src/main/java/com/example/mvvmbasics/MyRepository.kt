package com.example.mvvmbasics

import com.example.mvvmbasics.retrofit.AlbumService
import com.example.mvvmbasics.retrofit.Albums
import kotlinx.coroutines.delay
import retrofit2.Response

class MyRepository(private val retService: AlbumService) {

    suspend fun getAlbums(): Response<Albums> {
        delay(1500) // 통신 딜레이를 재현하기 위한 delay
        return retService.getAlbums()
    }

    suspend fun getSortedAlbums(userId: Int): Response<Albums> {
        delay(1500) // 통신 딜레이를 재현하기 위한 delay
        return retService.getSortedAlbums(userId)
    }
}