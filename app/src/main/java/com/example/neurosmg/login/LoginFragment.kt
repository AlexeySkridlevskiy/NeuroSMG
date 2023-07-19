package com.example.neurosmg.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentLoginBinding
import com.example.neurosmg.mainPage.MainPageUser

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }


    private var mainActivityListener: MainActivityListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }

        mainActivityListener?.updateToolbarState(ToolbarState.Initial)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        setHasOptionsMenu(true)

        binding.btnLogin.setOnClickListener {
            if (viewModel.canEnter(
                    binding.etLogin.text.toString(),
                    binding.etPassword.text.toString()
                )
            ) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.loginFragment, MainPageUser.newInstance())
                    .addToBackStack("LoginFragment").commit()
            } else {
                binding.etLogin.error = "Неверный логин или пароль"; // TODO:вынеси в ресурсы
                binding.etPassword.error = "Неверный логин или пароль"; // TODO:вынеси в ресурсы
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_menu, menu)
        val menuItem: MenuItem? = menu.findItem(R.id.ic_info)
        menuItem?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ic_info -> {
                infoDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun infoDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Вход") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные для входа вы можете запросить у человечка.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment();
    }
}