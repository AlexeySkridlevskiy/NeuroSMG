package com.example.neurosmg.preloader

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentInitialBinding
import com.example.neurosmg.login.LoginFragment
import com.example.neurosmg.mainPage.MainPageUser

class InitialFragment : Fragment() {

    lateinit var binding: FragmentInitialBinding

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[InitialViewModel::class.java]
    }

    private var mainActivityListener: MainActivityListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitialBinding.inflate(inflater)
        viewModel.sendTestRequest()
        viewModel.initialLiveData.observe(viewLifecycleOwner) { preloaderState ->
            when (preloaderState.state) {
                PreloaderState.StateInitial.LOADING -> {}

                PreloaderState.StateInitial.SUCCESS -> {
                    replaceFragment(MainPageUser.newInstance())
                }

                PreloaderState.StateInitial.ERROR -> {
                    replaceFragment(LoginFragment.newInstance())
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.Initial)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(Screen.PATIENTS)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = InitialFragment()
    }
}