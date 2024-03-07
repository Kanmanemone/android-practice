package com.example.eventwithview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eventwithview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding.myViewModel = viewModel
        viewModel.getCurrentCount().observe(this, Observer {
            binding.countText.text = it.toString()
        })

        displayToastEvent()
    }

    private fun displayToastEvent() {
        viewModel.toastMessage.observe(this, Observer {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}