package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.adapters.RecyclerServicesAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentServiceBinding
import dev.kuromiichi.apppeluqueria.listeners.ServiceOnClickListener
import dev.kuromiichi.apppeluqueria.models.Service
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ServiceFragment : Fragment(), ServiceOnClickListener {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { Firebase.firestore }
    private lateinit var mAdapter: RecyclerServicesAdapter
    private var services: List<Service> = listOf()
    private var servicesSelected = mutableListOf<Service>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

    }

    private fun setRecycler() {
        mAdapter = RecyclerServicesAdapter(services, this)
        binding.rvServices.apply {
            adapter = mAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }
        updateRecycler()
        setListeners()
    }

    private fun setListeners() {
        binding.fabGoToAppointment.setOnClickListener {
            if (servicesSelected.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_no_services_selected),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                //TODO Navigate to appointment with selected services as arguments
            }
        }
    }

    private fun updateRecycler() {
        // Get services from database with coroutines
        lifecycleScope.launch {
            services = db.collection("services").get().await()
                .toObjects(Service::class.java)
            mAdapter.setServices(services)
        }
    }


    override fun onServiceClick(service: Service) {
        if (!servicesSelected.contains(service)) {
            servicesSelected.add(service)
        }
        else {
            servicesSelected.remove(service)
        }
    }
}