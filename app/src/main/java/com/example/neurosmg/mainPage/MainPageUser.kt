package com.example.neurosmg.mainPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.databinding.FragmentMainPageUserBinding
import com.example.neurosmg.testsPage.TestsPage

class MainPageUser : Fragment() {
    lateinit var binding: FragmentMainPageUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageUserBinding.inflate(inflater)

        binding.btnGoTesting.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, TestsPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        @JvmStatic
        fun newInstance() = MainPageUser()
    }
}