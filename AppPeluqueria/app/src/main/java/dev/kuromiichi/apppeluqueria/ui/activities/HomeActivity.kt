package dev.kuromiichi.apppeluqueria.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.ActivityHomeBinding
import dev.kuromiichi.apppeluqueria.models.User
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackButtonBehavior()
        setNavigation()
        runBlocking { getUser() }
        setDrawerHeader()
    }

    private fun setBackButtonBehavior() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                    binding.drawerLayout.close()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController
        binding.navigationView.apply {
            setupWithNavController(navController)

            setNavigationItemSelectedListener {
                if (it.itemId == R.id.signOut) {
                    auth.signOut()

                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                } else {
                    NavigationUI.onNavDestinationSelected(it, navController)
                    binding.drawerLayout.close()

                    true
                }

            }
        }
    }

    private suspend fun getUser() {
        user = db.collection("users").document(auth.uid.toString())
            .get().await().toObject(User::class.java)
            ?: User("", "", "", "")
    }

    private fun setDrawerHeader() {
        binding.navigationView.getHeaderView(0).apply {
            findViewById<TextView>(R.id.username).text = user.name
            findViewById<TextView>(R.id.email).text = user.email
        }
    }

    fun updateDrawerHeader() {
        runBlocking { getUser() }
        setDrawerHeader()
    }
}