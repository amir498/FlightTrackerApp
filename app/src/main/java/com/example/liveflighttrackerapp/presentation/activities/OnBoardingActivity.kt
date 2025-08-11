package com.example.liveflighttrackerapp.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.databinding.ActivityOnBoardingBinding
import com.example.liveflighttrackerapp.presentation.adapter.OnBoardingPagerAdapter

class OnBoardingActivity : AppCompatActivity() {
    private val binding: ActivityOnBoardingBinding by lazy {
        ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    val adapter = OnBoardingPagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 1
        }

        viewPagerListener()
        viewListener()
    }

    private fun viewListener() {
        binding.btnGetStarted.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        }
    }

    private fun viewPagerListener() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.btnGetStarted.visibility = View.VISIBLE
                    }

                    1 -> {
                        binding.btnGetStarted.visibility = View.VISIBLE
                    }

                    2, 4 -> {
                        // Skip pages
                        binding.btnGetStarted.visibility = View.GONE
                    }

                    3 -> {
                        binding.btnGetStarted.visibility = View.VISIBLE
                    }

                    5 -> {
                        binding.btnGetStarted.text = getString(R.string.next)

                        binding.btnGetStarted.visibility = View.VISIBLE
                    }

                    6 -> {
//                        binding.btnGetStarted.text=getString(R.string.get_start)
                        binding.btnGetStarted.visibility = View.VISIBLE
//                        binding.pageIndicator.visibility = View.VISIBLE
                    }
                }


            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}