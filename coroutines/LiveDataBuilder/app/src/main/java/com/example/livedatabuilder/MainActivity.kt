package com.example.livedatabuilder

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {


    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // mainActivityViewModel.getUsers()
        mainActivityViewModel.users.observe(this, Observer { myUsers ->
            myUsers?.forEach {
                Log.i("interfacer_han", "name is ${it.name}")
            }
        })
    }
}