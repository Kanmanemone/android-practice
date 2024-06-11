package com.example.post

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface AlbumService {

    @GET("/albums")
    suspend fun getAlbums(): Response<Albums>

    @POST("/albums")
    suspend fun uploadAlbum(@Body album: AlbumsItem): Response<AlbumsItem>
}