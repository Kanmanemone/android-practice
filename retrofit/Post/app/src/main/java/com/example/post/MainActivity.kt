package com.example.post

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofitService =
            RetrofitInstance.getRetrofitInstance().create(AlbumService::class.java)

        val textView = findViewById<TextView>(R.id.textView)

        val responseLiveData: LiveData<Response<AlbumsItem>> = liveData {
            val album = AlbumsItem(7, 12, "Post practice") // AlbumsItem 객체를 하드 코딩
            val response = retrofitService.uploadAlbum(album)
            emit(response)
        }

        responseLiveData.observe(this, Observer {
            displayListOnScreen(textView, it)
        })
    }

    private fun displayListOnScreen(textView: TextView, response: Response<AlbumsItem>) {
        val item = response.body()

        if (item != null) {
            val result =
                "User id : ${item.userId}" + "\n" +
                "Album id : ${item.id}" + "\n" +
                "Album Title : ${item.title}" + "\n\n\n"
e
            textView.append(result)
        }
    }
}