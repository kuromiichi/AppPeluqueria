package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.FragmentProfileBinding
import dev.kuromiichi.apppeluqueria.models.User
import dev.kuromiichi.apppeluqueria.ui.activities.HomeActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            user = setUser()
        }
        setButtons()
        setTexts()
    }

    private suspend fun setUser(): User {
        return db.collection("users").document(auth.uid.toString()).get().await()
            .toObject(User::class.java)!!
    }

    private fun setButtons() {
        binding.buttonChangePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }

        binding.buttonUpdate.setOnClickListener {
            updateUser()
        }
    }

    private fun updateUser() {
        if (binding.tilPassword.editText?.text.toString().isBlank()) {
            Toast.makeText(
                this.context, getString(R.string.toast_mandatory_fields), Toast.LENGTH_SHORT
            ).show()
            return
        }
        val credentials = EmailAuthProvider.getCredential(
            user.email, binding.tilPassword.editText?.text.toString()
        )

        if (user.email == binding.tilEmail.editText?.text.toString()
            && user.name == binding.tilName.editText?.text.toString()
            && user.phone == binding.tilPhone.editText?.text.toString()
        ) {
            Toast.makeText(
                this.context, getString(R.string.profile_change_fields), Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (binding.tilEmail.editText?.text.toString() != user.email) {
            auth.currentUser?.reauthenticate(credentials)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    updateUserData()
                    auth.currentUser?.verifyBeforeUpdateEmail(binding.tilEmail.editText?.text.toString())
                } else {
                    Toast.makeText(
                        this.context, getString(R.string.toast_wrong_password), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            auth.currentUser?.reauthenticate(credentials)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    updateUserData()
                } else {
                    Toast.makeText(
                        this.context, getString(R.string.toast_wrong_password), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUserData() {
        db.collection("users").document(auth.uid.toString()).update(
            "email",
            binding.tilEmail.editText?.text.toString(),
            "name",
            binding.tilName.editText?.text.toString(),
            "phone",
            binding.tilPhone.editText?.text.toString()
        ).addOnSuccessListener {
            Toast.makeText(
                this.context, getString(R.string.profile_updated), Toast.LENGTH_SHORT
            ).show()
            (activity as HomeActivity).updateDrawerHeader()
        }
    }

    private fun setTexts() {
        binding.tilEmail.editText?.setText(user.email)
        binding.tilName.editText?.setText(user.name)
        binding.tilPhone.editText?.setText(user.phone)
    }
}