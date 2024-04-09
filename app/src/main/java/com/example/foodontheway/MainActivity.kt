package com.example.foodontheway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.foodontheway.Fragments.NotificationFragment
import com.example.foodontheway.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavHostFragment using the supportFragmentManager
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Set up the BottomNavigationView with the NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        //setting up with notification bell
        binding.notificationBell.setOnClickListener{
            val bottomSheetDialog = NotificationFragment()
            bottomSheetDialog.show(supportFragmentManager,"Test")
        }
    }
}