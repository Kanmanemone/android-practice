package com.example.roomupdate

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomupdate.databinding.ActivityMainBinding
import com.example.roomupdate.db.User
import com.example.roomupdate.db.UserDatabase
import com.example.roomupdate.db.UserRepository

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

        initRecyclerView()
    }

    fun initRecyclerView() {
        binding.userRecycler.layoutManager = LinearLayoutManager(this)
        displayUsersList()
    }

    private fun displayUsersList() {
        userViewModel.users.observe(this, Observer {
            Log.i("interfacer_han", it.toString())
            binding.userRecycler.adapter =
                MyRecyclerViewAdapter(it) { selectedItem: User -> listItemClicked(selectedItem) }
        })
    }

    private fun listItemClicked(subscriber: User) {
        Toast.makeText(this, "selected name is ${subscriber.name}", Toast.LENGTH_LONG).show()
        userViewModel.initUpdateAndDelete(subscriber)
    }
}