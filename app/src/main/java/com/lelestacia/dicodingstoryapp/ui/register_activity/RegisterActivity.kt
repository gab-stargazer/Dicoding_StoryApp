package com.lelestacia.dicodingstoryapp.ui.register_activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lelestacia.dicodingstoryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    private fun registerAccount() {
        val username = binding.edtUsername.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (username.isEmpty())
            binding.edtUsername.error = "Username tidak boleh kosong"
        if (email.isEmpty())
            binding.edtEmail.error = "Email tidak boleh kosong"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            binding.edtEmail.error = "Email tidak valid"
        if (password.length < 6 || password.isEmpty())
            binding.edtPassword.error = "Password tidak boleh kosong atau kurang dari 6 karakter"
        if (username.isNotEmpty() && email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches() && password.length < 6)
            viewModel.signUpWithEmailAndPassword(username,email, password)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.tvLogin.id -> onBackPressed()
            binding.btnRegister.id -> registerAccount()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}