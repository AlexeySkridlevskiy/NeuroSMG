package com.example.neurosmg.login

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentLoginBinding
import com.example.neurosmg.mainPage.MainPageUser

class LoginFragment : Fragment(){
    lateinit var binding: FragmentLoginBinding
    private lateinit var toolbar: Toolbar
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        toolbar = activity?.findViewById(R.id.toolbar)!!
        binding.btnLogin.setOnClickListener {
            if (viewModel.canEnter(binding.etLogin.text.toString(), binding.etPassword.text.toString())){
                parentFragmentManager.beginTransaction().replace(R.id.loginFragment, MainPageUser.newInstance()).addToBackStack("LoginFragment").commit()
            }else{
                binding.etLogin.error = "Неверный логин или пароль";
                binding.etPassword.error = "Неверный логин или пароль";
            }
        }
        toolbar.setNavigationOnClickListener{
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Вход")
            alertDialogBuilder.setMessage("Данные для входа вы можете запросить у человечка.")
            alertDialogBuilder.setPositiveButton("Окей") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
            //вынести в viewModel???
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "NeuroSMG"

        toolbar.setNavigationIcon(R.drawable.info_icon)
    }

    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment();
    }
}