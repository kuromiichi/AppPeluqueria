package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.FragmentChangePasswordBinding
import dev.kuromiichi.apppeluqueria.databinding.FragmentProfileBinding
import dev.kuromiichi.apppeluqueria.models.User


class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { Firebase.auth }
    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()

    }

    private fun setButtons() {
        binding.buttonUpdate.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val NewPw = binding.tilNewPw1.editText?.text.toString()

        if (binding.tilNewPw1.editText?.text.toString() != binding.tilNewPw2.editText?.text.toString()) {
            Toast.makeText(
                this.context,
                getString(R.string.toast_passwords_dont_match),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        else {
            val credentials: AuthCredential = EmailAuthProvider.getCredential(
                user.email, binding.tilActualPw.editText?.text.toString()
            )

            auth.currentUser?.reauthenticate(credentials)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.updatePassword(NewPw)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this.context,
                                getString(R.string.toast_password_changed),
                                Toast.LENGTH_SHORT
                            ).show()
                            val navController = findNavController()
                            navController.popBackStack(R.id.profileFragment, false )
                            navController.navigate(R.id.profileFragment)
                        }
                    }
                }
            }
        }
    }
}