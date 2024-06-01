package com.example.retrofitbasics

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

        val responseLiveData: LiveData<Response<Albums>> = liveData {
            val response1 = retrofitService.getAlbums()
            val response2 = retrofitService.getSortedAlbums(7)
            emit(response1)
        }

        responseLiveData.observe(this, Observer {
            appendListOnTextView(textView, it)
        })
    }

    private fun appendListOnTextView(textView: TextView, list: Response<Albums>) {
        val albumsList = list.body()?.listIterator()
        if (albumsList != null) {

            while (albumsList.hasNext()) {
                val albumsItem = albumsList.next()
                val result =
                    "User id : ${albumsItem.userId}" + "\n" +
                    "Album id : ${albumsItem.id}" + "\n" +
                    "Album Title : ${albumsItem.title}" + "\n\n\n"

                textView.append(result)
            }
        }
    }
}