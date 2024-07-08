package com.example.testingviewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.testingviewmodel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calculation: Calculation
    private lateinit var viewModelFactory: CalculationViewModelFactory
    private lateinit var viewModel: CalculationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        calculation = MyCalculation()
        viewModelFactory = CalculationViewModelFactory(calculation)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CalculationViewModel::class.java)
        binding.viewModel = viewModel // activity_main의 viewModel에 MainActivity의 viewModel 삽입
        binding.lifecycleOwner = this
    }
}