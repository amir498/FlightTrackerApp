package com.example.liveflighttrackerapp.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.databinding.ActivityLanguageSelectionBinding

class LanguageSelectionActivity : AppCompatActivity() {
    private val binding: ActivityLanguageSelectionBinding by lazy {
        ActivityLanguageSelectionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLangT.setOnClickListener {
            Intent(this, OnBoardingActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}