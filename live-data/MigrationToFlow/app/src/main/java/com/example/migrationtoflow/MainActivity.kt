package com.example.migrationtoflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.migrationtoflow.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

/*
        viewModel.getCurrentCount().observe(this, Observer {
            binding.countText.text = it.toString()
        })
*/
        lifecycleScope.launch {
            viewModel.getCurrentCount().collect {
                binding.countText.text = it.toString()
            }
        }

        binding.countButton.setOnClickListener {
            viewModel.updateCount()
        }
    }
}