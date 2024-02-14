package dev.kuromiichi.apppeluqueria.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.ActivityRegisterBinding
import dev.kuromiichi.apppeluqueria.models.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

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

            when {
                fullName.isBlank()
                    || email.isBlank()
                    || phone.isBlank()
                    || password.isBlank()
                    || confirmPassword.isBlank()
                -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_mandatory_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_passwords_not_matching),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_password_too_short),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                phone.length != 9 -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_invalid_phone),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !email.matches("^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}\$".toRegex()) -> {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        createUser()

                        Toast.makeText(
                            this,
                            getString(R.string.toast_register_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else Toast.makeText(
                        this,
                        getString(R.string.toast_register_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }
    }

    private fun createUser() {
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .set(
                User(
                    name = binding.tilName.editText?.text.toString(),
                    email = binding.tilEmail.editText?.text.toString(),
                    phone = binding.tilPhone.editText?.text.toString(),
                    uid = auth.currentUser!!.uid,
                )
            )
    }
}