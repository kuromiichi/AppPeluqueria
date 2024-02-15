package dev.kuromiichi.apppeluqueria.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.adapters.RecyclerAppointmentAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentHomeBinding
import dev.kuromiichi.apppeluqueria.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueria.models.Appointment

class HomeFragment : Fragment(), AppointmentOnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    private lateinit var adapter: RecyclerAppointmentAdapter
    private var appointments = emptyList<Appointment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearBackStack()
        setRecycler()

        binding.fabGoToService.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_serviceFragment)
        }
    }

    private fun clearBackStack() {
        val navController = findNavController()
        navController.popBackStack(navController.graph.startDestinationId, false)
    }

    private fun setRecycler() {
        adapter = RecyclerAppointmentAdapter(appointments, this)
        binding.rvAppointments.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
        updateRecycler()
    }

    private fun updateRecycler() {
        // TODO Get appointments from database with coroutines,
        // TODO hay una función en el recycler adapter para updatearlo
    }

    override fun onAppointmentClick(appointment: Appointment) {
        //TODO Dialogo para eliminar la cita
    }
}
