package com.example.neurosmg

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.aboutProgramPage.AboutProgramPage
import com.example.neurosmg.databinding.ActivityMainBinding
import com.example.neurosmg.doctorProfile.DoctorProfile
import com.example.neurosmg.login.LoginFragment
import com.example.neurosmg.patientTestList.PatientListFragment
import com.example.neurosmg.patientTestList.StatePatientViewModel

class MainActivity : AppCompatActivity(), MainActivityListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var fragment: Fragment
    private var currentFragmentTag: String? = null

    private val viewModelState by lazy {
        ViewModelProvider(this)[StatePatientViewModel::class.java]
    }

    private val menuActions = mapOf(
        R.id.tests to {
            replaceFragment(PatientListFragment.newInstance(), Screen.PATIENTS)
        },
        R.id.questionnaires to {
            Toast.makeText(this, "Страница 'Опросники' находится в разработке", Toast.LENGTH_SHORT)
                .show()
        },
        R.id.patients to {
            navigateToPatientTestList(false)
        },
        R.id.archive to {
            navigateToPatientTestList(true)
        },
        R.id.about_program to {
            replaceFragment(AboutProgramPage.newInstance(), Screen.ABOUT_APP)
        }
    )

    private fun navigateToPatientTestList(isArchive: Boolean) {
        if (isArchive) {
            viewModelState.navToArchive()
        } else {
            viewModelState.navToPatientList()
        }

        fragment = PatientListFragment()
        replaceFragment(fragment, if (isArchive) Screen.ARCHIVE else Screen.PATIENTS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(KeyOfArgument.KEY_OF_FRAGMENT) ?: ""
        }

        if (currentFragmentTag != null) {
            val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            if (fragment != null) {
                setupFragment(fragment, currentFragmentTag ?: Screen.LOGIN)
            }
        } else {
            setupFragment(LoginFragment.newInstance(), Screen.LOGIN)
        }

        setupDrawerLayout()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(KeyOfArgument.KEY_OF_FRAGMENT, currentFragmentTag)
    }

    private fun setupFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, tag)
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
        when (toolbarState) {
            ToolbarState.Initial -> setupToolbarForInitial()
            ToolbarState.MainPage -> setupToolbarForMainPage()
            ToolbarState.DoctorProfile -> setupToolbarForDoctorProfile()
            ToolbarState.TestPage -> setupToolbarForTestPage()
            ToolbarState.PatientList -> setupToolbarForPatientList()
            ToolbarState.FOTTest -> setupToolbarForFOTTest()
            ToolbarState.RATTest -> setupToolbarForRATTest()
            ToolbarState.IATTest -> setupToolbarForIATTest()
            ToolbarState.IATTest2 -> setupToolbarForIATTest2()
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
            toolbar.title = null
            toolbar.navigationIcon = getDrawable(R.drawable.ic_menu)
            toolbarTitleCenter.text = getString(R.string.lbl_title_main)
            toolbarTitleCenter.isVisible = true
            toolbarSubtitle.isVisible = false
            toolbar.inflateMenu(R.menu.main_menu)
            idSettings.isVisible = true
            subtitle.isVisible = false

            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            idSettings.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, DoctorProfile.newInstance())
                    .addToBackStack(Screen.DOCTOR_PROFILE)
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

    private fun setupToolbarForIATTest2() {
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
        currentFragmentTag = tagBackStack
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(tagBackStack)
            .commit()
    }

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 123
    }
}