package com.example.neurosmg

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.neurosmg.databinding.ActivityMainBinding
import com.example.neurosmg.doctorProfile.DoctorProfile
import com.example.neurosmg.login.LoginFragment

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupFragment()
        setupDrawerLayout()
    }

    private fun setupFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, LoginFragment.newInstance())
            .commit()
    }

    private fun setupDrawerLayout() {
        drawerLayout = binding.drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun updateToolbarState(toolbarState: ToolbarState) {
        with(binding.includeToolbar) {
            when (toolbarState) {
                ToolbarState.Initial -> setupToolbarForInitial()
                ToolbarState.MainPage -> setupToolbarForMainPage()
                ToolbarState.DoctorProfile -> setupToolbarForDoctorProfile()
                ToolbarState.TestPage -> setupToolbarForTestPage()
                ToolbarState.PatientList -> setupToolbarForPatientList()
            }
        }
    }

    private fun setupToolbarForInitial() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = null
            toolbarTitleCenter.isVisible = false
            idSettings.isVisible = false
        }
    }

    private fun setupToolbarForMainPage() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbarTitleCenter.text = getString(R.string.lbl_title_main)
            toolbarTitleCenter.isVisible = true
            toolbar.title = null
            toolbar.inflateMenu(R.menu.main_menu)
            idSettings.isVisible = true

            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            idSettings.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.loginFragment, DoctorProfile.newInstance())
                    .addToBackStack(Screen.MAIN_PAGE)
                    .commit()
            }
        }
    }

    private fun setupToolbarForDoctorProfile() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.title_doctor_profile)
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForTestPage() {
        // Add code here for TestPage state
    }

    private fun setupToolbarForPatientList() {
        // Add code here for PatientList state
    }
}