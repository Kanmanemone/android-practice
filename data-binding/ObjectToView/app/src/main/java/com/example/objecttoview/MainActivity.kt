package com.example.objecttoview

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objecttoview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        onShowBookInfoButtonClick(binding.buttonBook1, getWalden())
        onShowBookInfoButtonClick(binding.buttonBook2, getTheLittlePrince())
        onShowBookInfoButtonClick(binding.buttonBook3, getDetailsOfTokyo())
        onShowBookInfoButtonClick(binding.buttonBook4, getFlipThinking())
    }

    private fun onShowBookInfoButtonClick(button: Button, book: Book) {
        button.setOnClickListener {
            binding.book = book
        }
    }

    private fun getWalden(): Book {
        return Book("월든", "David Thoreau", 1854)
    }

    private fun getTheLittlePrince(): Book {
        return Book("어린 왕자", "Saint-Exupéry", 1943)
    }

    private fun getDetailsOfTokyo(): Book {
        return Book("도쿄의 디테일", "생각노트", 2018)
    }

    private fun getFlipThinking(): Book {
        return Book("플립 싱킹", "Berthold Gunster", 2023)
    }
}