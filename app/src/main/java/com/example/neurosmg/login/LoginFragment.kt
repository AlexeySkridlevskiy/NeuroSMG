package com.example.neurosmg.login

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentLoginBinding
import com.example.neurosmg.mainPage.MainPageUser

class LoginFragment : Fragment(){
    lateinit var binding: FragmentLoginBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        setHasOptionsMenu(true)

        binding.btnLogin.setOnClickListener {
            if (viewModel.canEnter(binding.etLogin.text.toString(), binding.etPassword.text.toString())){
                parentFragmentManager.beginTransaction().replace(R.id.loginFragment, MainPageUser.newInstance()).addToBackStack("LoginFragment").commit()
            }else{
                binding.etLogin.error = "Неверный логин или пароль";
                binding.etPassword.error = "Неверный логин или пароль";
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_menu, menu)
        val menuItem: MenuItem? = menu.findItem(R.id.ic_info)
        menuItem?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ic_info -> {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Вход")
                alertDialogBuilder.setMessage("Данные для входа вы можете запросить у человечка.")
                alertDialogBuilder.setPositiveButton("Окей") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment();
    }
}