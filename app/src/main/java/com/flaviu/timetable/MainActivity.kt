package com.flaviu.timetable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.flaviu.timetable.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        NavigationUI.setupActionBarWithNavController(this, this.findNavController(R.id.nav_host_fragment))

    }

    override fun onSupportNavigateUp(): Boolean {
        val returnValue = this.findNavController(R.id.nav_host_fragment).navigateUp()
        hideKeyboard(this)
        return returnValue
    }
}