package dev.kuromiichi.apppeluqueria.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    private fun setButtons() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.tilName.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val phone = binding.tilPhone.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            val confirmPassword = binding.tilConfirmPassword.editText?.text.toString()

            if (fullName.isBlank()
                || email.isBlank()
                || phone.isBlank()
                || password.isBlank()
                || confirmPassword.isBlank()
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_mandatory_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_passwords_not_matching),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // add to database
                    startActivity(Intent(this, MainActivity::class.java))
                } else Toast.makeText(
                    this,
                    getString(R.string.toast_register_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}