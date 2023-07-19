package com.example.neurosmg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.example.neurosmg.databinding.ActivityMainBinding
import com.example.neurosmg.login.LoginFragment
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.doctorProfile.DoctorProfile
import com.example.neurosmg.login.LoginViewModel
import com.example.neurosmg.testsPage.TestsPage

class MainActivity : AppCompatActivity(), MainActivityListener {

    lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, LoginFragment.newInstance())
            .commit()

        drawerLayout = binding.drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return actionBarDrawerToggle.onOptionsItemSelected(item)
    }

    override fun updateToolbarState(toolbarState: ToolbarState) = with(binding){
        when (toolbarState) {
            ToolbarState.Initial -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                setSupportActionBar(binding.includeToolbar.toolbar)
                includeToolbar.toolbar.navigationIcon = null
                includeToolbar.idSettings.isVisible = false
            }
            ToolbarState.MainPage -> {
                includeToolbar.toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
                includeToolbar.toolbarTitleCenter.text = getString(R.string.lbl_title_main)
                includeToolbar.toolbarTitleCenter.isVisible = true
                includeToolbar.toolbar.title = null
                includeToolbar.toolbar.inflateMenu(R.menu.main_menu)
                includeToolbar.idSettings.isVisible = true

                includeToolbar.toolbar.setNavigationOnClickListener {
                    drawerLayout.openDrawer(GravityCompat.START)
                }

                includeToolbar.idSettings.setOnClickListener {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, DoctorProfile.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                }
            }
            ToolbarState.DoctorProfile -> {
                includeToolbar.toolbarTitleCenter.text = getString(R.string.title_doctor_profile)
                includeToolbar.idSettings.isVisible = false
                includeToolbar.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)

                includeToolbar.toolbar.setNavigationOnClickListener {
                    onBackPressed()
                }
            }
        }
    }
}