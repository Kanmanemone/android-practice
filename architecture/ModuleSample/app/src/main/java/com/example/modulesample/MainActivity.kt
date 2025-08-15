package com.example.modulesample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbasics.R
import com.example.mvvmbasics.databinding.ActivityMainBinding
import com.example.modulesample.retrofit.AlbumService
import com.example.modulesample.retrofit.Albums
import com.example.modulesample.retrofit.RetrofitInstance
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var repository: MyRepository
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = MyRepository(RetrofitInstance.getRetrofitInstance().create(AlbumService::class.java))
        viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.userId.observe(this, Observer {
            binding.textView.text = ""
        })

        viewModel.albums.observe(this, Observer {
            appendListOnTextView(binding.textView, it)
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