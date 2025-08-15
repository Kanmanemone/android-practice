package com.example.network.retrofit


import com.google.gson.annotations.SerializedName

data class AlbumsItem(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,
)

/*
{
    "userId": 1,
    "id": 1,
    "title": "Album Title"
}

↑ 이 클래스는 위와 같은 JSON 데이터를, 아래와 같은 Kotlin 객체로 바꾼다. ↓

val myAlbum = AlbumsItem(id = 1, title = "Album Title", userId = 1)
*/