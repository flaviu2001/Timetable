package com.flaviu.timetable.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.flaviu.timetable.R
import com.flaviu.timetable.databinding.ActivityMainBinding
import com.flaviu.timetable.hideKeyboard
import com.flaviu.timetable.setAccentColor
import com.flaviu.timetable.setBackgroundColor

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setBackgroundColor(this)
        setAccentColor(this)
        NavigationUI.setupActionBarWithNavController(this, this.findNavController(R.id.nav_host_fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val returnValue = this.findNavController(R.id.nav_host_fragment).navigateUp()
        hideKeyboard(this)
        return returnValue
    }
}