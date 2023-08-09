package com.example.neurosmg

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
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
    private val bundle = Bundle()
    private lateinit var fragment: Fragment

    private val menuActions = mapOf(
        R.id.tests to {
            replaceFragment(TestsPage.newInstance(), Screen.MAIN_PAGE)
        },
        R.id.questionnaires to {
//            replaceFragment(TestsPage.newInstance(), Screen.MAIN_PAGE)
            Toast.makeText(this, "Страница 'Опросники' находится в разработке", Toast.LENGTH_SHORT).show()
        },
        R.id.patients to {
            bundle.putBoolean(KeyOfArgument.KEY_OF_MAIN_TO_PATIENT, true)
            fragment = PatientTestList.newInstance()
            fragment.arguments = bundle
            replaceFragment(fragment, Screen.MAIN_PAGE)
        },
        R.id.archive to {
            bundle.putBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE, true)
            fragment = PatientTestList.newInstance()
            fragment.arguments = bundle
            replaceFragment(fragment, Screen.MAIN_PAGE)
        },
        R.id.about_program to {
            replaceFragment(AboutProgramPage.newInstance(), Screen.MAIN_PAGE)
        }
    )

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
            menuActions[menuItem.itemId]?.invoke()
            binding.drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun updateToolbarState(toolbarState: ToolbarState) {
        Log.d("toolbarTest", "updateToolbarState: $toolbarState")
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
            ToolbarState.HideToolbar -> visibilityToolbar(false)
        }
    }

    private fun visibilityToolbar(isVisible: Boolean) {
        binding.includeToolbar.root.isVisible = isVisible
    }

    private fun setupToolbarForInitial() {
        with(binding.includeToolbar) {
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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
            visibilityToolbar(true)
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

    private fun replaceFragment(fragment: Fragment, tagBackStack: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFragment, fragment)
            .addToBackStack(tagBackStack)
            .commit()
    }
}