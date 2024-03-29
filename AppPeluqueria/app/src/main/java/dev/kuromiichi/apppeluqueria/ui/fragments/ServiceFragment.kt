package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.adapters.RecyclerServicesAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentServiceBinding
import dev.kuromiichi.apppeluqueria.listeners.ServiceOnClickListener
import dev.kuromiichi.apppeluqueria.models.Service

class ServiceFragment : Fragment(), ServiceOnClickListener {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    private lateinit var adapter: RecyclerServicesAdapter
    private var services = emptyList<Service>()
    private var selectedServices = mutableSetOf<Service>()

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

        selectedServices.clear()
        setRecycler()
    }

    private fun setRecycler() {
        adapter = RecyclerServicesAdapter(services, this)
        binding.rvServices.apply {
            adapter = this@ServiceFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
        updateRecycler()
        setListeners()
    }

    private fun updateRecycler() {
        db.collection("services").get().addOnSuccessListener { result ->
            services = result.toObjects(Service::class.java)
            adapter.setServices(services)
        }
    }

    private fun setListeners() {
        binding.fabGoToAppointment.setOnClickListener {
            if (selectedServices.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_no_services_selected),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val action = ServiceFragmentDirections.actionServiceFragmentToAppointmentFragment(
                    selectedServices.toTypedArray()
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onServiceClick(service: Service) {
        if (service in selectedServices) {
            selectedServices.remove(service)
        } else {
            selectedServices.add(service)
        }
    }
}