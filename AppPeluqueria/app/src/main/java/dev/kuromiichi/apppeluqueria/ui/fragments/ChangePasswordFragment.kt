package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.FragmentChangePasswordBinding
import dev.kuromiichi.apppeluqueria.models.User
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking { user = setUser() }
        setButtons()
    }

    private suspend fun setUser(): User {
        return db.collection("users").document(auth.uid.toString()).get().await()
            .toObject(User::class.java)!!
    }

    private fun setButtons() {
        binding.buttonUpdate.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val newPassword = binding.tilNewPw1.editText?.text.toString()

        if (binding.tilNewPw1.editText?.text.toString() != binding.tilNewPw2.editText?.text.toString()) {
            Toast.makeText(
                this.context, getString(R.string.toast_passwords_dont_match), Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (binding.tilCurrentPw.editText?.text.toString()
                .isBlank() || binding.tilNewPw1.editText?.text.toString()
                .isBlank() || binding.tilNewPw2.editText?.text.toString().isBlank()
        ) {
            Toast.makeText(
                this.context, getString(R.string.toast_mandatory_fields), Toast.LENGTH_SHORT
            ).show()
            return
        }

        val credentials = EmailAuthProvider.getCredential(
            user.email, binding.tilCurrentPw.editText?.text.toString()
        )

        auth.currentUser?.reauthenticate(credentials)?.addOnCompleteListener {
            if (it.isSuccessful) {
                auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this.context,
                            getString(R.string.toast_password_changed),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
                    } else {
                        Toast.makeText(
                            this.context,
                            getString(R.string.toast_password_change_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this.context, getString(R.string.toast_wrong_password), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}