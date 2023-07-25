package com.example.neurosmg

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.neurosmg.aboutProgramPage.AboutProgramPage
import com.example.neurosmg.databinding.ActivityMainBinding
import com.example.neurosmg.doctorProfile.DoctorProfile
import com.example.neurosmg.login.LoginFragment
import com.example.neurosmg.patientTestList.Patient
import com.example.neurosmg.patientTestList.PatientTestList
import com.example.neurosmg.testsPage.TestsPage

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

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tests -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, TestsPage.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                    return@setNavigationItemSelectedListener true
                }

                R.id.questionnaires -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, TestsPage.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                    Toast.makeText(this, "Тут нужен опросник", Toast.LENGTH_SHORT).show()
                    return@setNavigationItemSelectedListener true
                }

                R.id.patients -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, PatientTestList.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                    return@setNavigationItemSelectedListener true
                }

                R.id.archive -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, PatientTestList.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                    Toast.makeText(this, "Тут нужен архив", Toast.LENGTH_SHORT).show()
                    return@setNavigationItemSelectedListener true
                }

                R.id.about_program -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginFragment, AboutProgramPage.newInstance())
                        .addToBackStack(Screen.MAIN_PAGE)
                        .commit()
                    return@setNavigationItemSelectedListener true
                }

                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }
    }

    override fun updateToolbarState(toolbarState: ToolbarState) {
        when (toolbarState) {
            ToolbarState.Initial -> setupToolbarForInitial()
            ToolbarState.MainPage -> setupToolbarForMainPage()
            ToolbarState.DoctorProfile -> setupToolbarForDoctorProfile()
            ToolbarState.TestPage -> setupToolbarForTestPage()
            ToolbarState.PatientList -> setupToolbarForPatientList()
            ToolbarState.FOTTest -> setupToolbarForFOTTest()
            ToolbarState.RATTest -> setupToolbarForRATTest()
            ToolbarState.IATTest -> setupToolbarForIATTest()
            ToolbarState.GNGTest -> setupToolbarForGNGTest()
            ToolbarState.SCTTest -> setupToolbarForSCTTest()
            ToolbarState.TMTTest -> setupToolbarForTMTTest()
            ToolbarState.CBTTest -> setupToolbarForCBTTest()
            ToolbarState.MRTTest -> setupToolbarForMRTTest()
            ToolbarState.PatientProfile -> setupToolbarForPatientProfile()
            ToolbarState.Archive -> setupToolbarForArchive()
            ToolbarState.AboutProgramPage -> setupToolbarForAboutProgramPage()
        }
    }

    private fun setupToolbarForInitial() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = null
            toolbarTitleCenter.isVisible = false
            toolbarSubtitle.isVisible = false
            subtitle.isVisible = false
            idSettings.isVisible = false
        }
    }

    private fun setupToolbarForMainPage() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbarTitleCenter.text = getString(R.string.lbl_title_main)
            toolbarTitleCenter.isVisible = true
            toolbarSubtitle.isVisible = false
            toolbar.title = null
            toolbar.inflateMenu(R.menu.main_menu)
            idSettings.isVisible = true
            subtitle.isVisible = false

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

    private fun setupToolbarForAboutProgramPage() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.about_program)
            toolbarSubtitle.isVisible = false
            idSettings.isVisible = false
            subtitle.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForDoctorProfile() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.title_doctor_profile)
            toolbarSubtitle.isVisible = false
            idSettings.isVisible = false
            subtitle.isVisible = false
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForTestPage() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbarTitleCenter.text = getString(R.string.tests)
            toolbarSubtitle.isVisible = false
            idSettings.isVisible = false
            subtitle.isVisible = false
            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupToolbarForPatientList() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarSubtitle.isVisible = false
            toolbarTitleCenter.text = getString(R.string.list_of_patients)
            idSettings.isVisible = false
            subtitle.isVisible = false
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForPatientProfile() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarSubtitle.isVisible = false
            toolbarTitleCenter.text = getString(R.string.profile_of_patient)
            idSettings.isVisible = false
            subtitle.isVisible = false
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForArchive() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbarSubtitle.isVisible = false
            toolbarTitleCenter.text = getString(R.string.archive)
            idSettings.isVisible = false
            subtitle.isVisible = false
            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupToolbarForFOTTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.fot_test)
            subtitle.text = getString(R.string.fot_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForRATTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.rat_test)
            subtitle.text = getString(R.string.rat_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForIATTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.iat_test)
            subtitle.text = getString(R.string.iat_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForGNGTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.gng_test)
            subtitle.text = getString(R.string.gng_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForSCTTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.sct_test)
            subtitle.text = getString(R.string.sct_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForTMTTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.tmt_test)
            subtitle.text = getString(R.string.tmt_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForCBTTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.cbt_test)
            subtitle.text = getString(R.string.cbt_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupToolbarForMRTTest() {
        with(binding.includeToolbar) {
            setSupportActionBar(toolbar)
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
            toolbarTitleCenter.text = getString(R.string.mrt_test)
            subtitle.text = getString(R.string.mrt_test_subtitle)
            subtitle.isVisible = true
            idSettings.isVisible = false

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
}