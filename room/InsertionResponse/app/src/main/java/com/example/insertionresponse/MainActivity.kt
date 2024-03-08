package com.example.insertionresponse

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.insertionresponse.databinding.ActivityMainBinding
import com.example.insertionresponse.db.UserDatabase
import com.example.insertionresponse.db.UserRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dao = UserDatabase.getInstance(application).userDAO
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        displayUsersList()
        displayToastEvent()
    }

    private fun displayUsersList() {
        userViewModel.users.observe(this, Observer {
            Log.i("interfacer_han", it.toString())
        })
    }

    private fun displayToastEvent() {
        userViewModel.toastMessage.observe(this, Observer {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}