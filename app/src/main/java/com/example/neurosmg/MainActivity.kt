package com.example.neurosmg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.neurosmg.databinding.ActivityMainBinding
import com.example.neurosmg.login.LoginFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, LoginFragment.newInstance())
            .commit()


    }
}