package com.example.loggingandtimeoutmanagement

import com.example.loggingandtimeoutmanagement.Albums
import retrofit2.Response
import retrofit2.http.GET


interface AlbumService {

    @GET("/albums")
    suspend fun getAlbums(): Response<Albums>
}