package com.example.livedatabasics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.livedatabasics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
/*
        val countObserver = object : Observer<Int> {
            override fun onChanged(newValue: Int) {
                binding.countText.text = newValue.toString()
            }
        }
        viewModel.getCurrentCount().observe(this, countObserver)
*/
        viewModel.getCurrentCount().observe(this, Observer {
            binding.countText.text = it.toString()
        })
        binding.countButton.setOnClickListener {
            viewModel.updateCount()
        }
    }
}