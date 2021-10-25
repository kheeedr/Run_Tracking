package com.khedr.runtracking.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.Theme_RunTracking)

        super.onCreate(savedInstanceState)
        b = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        b.navBar.visibility = View.GONE
        b.fabMainPage.setOnClickListener { b.navBar.selectedItemId = R.id.homeFragment }

        navController = findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(b.navBar, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.profile_fragment, R.id.program_fragment -> {
                    b.navBar.visibility = View.VISIBLE
                    b.fabMainPage.visibility = View.VISIBLE

                }
                else -> {
                    b.navBar.visibility = View.GONE
                    b.fabMainPage.visibility = View.GONE
                }
            }
        }
    }
}