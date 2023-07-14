package com.example.neurosmg.mainPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentLoginBinding
import com.example.neurosmg.databinding.FragmentMainPageUserBinding

class MainPageUser : Fragment() {
    lateinit var binding: FragmentMainPageUserBinding

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageUserBinding.inflate(inflater)
        toolbar = activity?.findViewById(R.id.toolbar)!!
        toolbar.setNavigationOnClickListener{
            toolbar.title = "Click"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "Главная"
        toolbar.setNavigationIcon(R.drawable.burger_icon)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainPageUser()
    }
}