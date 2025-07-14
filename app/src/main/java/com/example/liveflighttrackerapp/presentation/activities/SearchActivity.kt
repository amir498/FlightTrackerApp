package com.example.liveflighttrackerapp.presentation.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.databinding.ActivityMainBinding
import com.example.liveflighttrackerapp.databinding.ActivitySearchBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.utils.hideSystemUI
import com.example.liveflighttrackerapp.presentation.utils.setScreenDisplay

class SearchActivity : AppCompatActivity() {

    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
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
        window.apply {
            statusBarColor = ContextCompat.getColor(this@SearchActivity, R.color.con_background_color)
        }
        setScreenDisplay()
        window.hideSystemUI()


    }
}