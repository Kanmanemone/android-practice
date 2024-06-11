package com.example.loggingandtimeoutmanagement

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
            val response = retrofitService.getAlbums()
            emit(response)
        }

        responseLiveData.observe(this, Observer {
            displayListOnScreen(textView, it)
        })
    }

    private fun displayListOnScreen(textView: TextView, list: Response<Albums>) {
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