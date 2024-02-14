package dev.kuromiichi.apppeluqueria.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.adapters.RecyclerAppointmentAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentHomeBinding
import dev.kuromiichi.apppeluqueria.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueria.models.Appointment
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), AppointmentOnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: RecyclerAppointmentAdapter
    private var appointments: List<Appointment> = listOf()
    private val db by lazy { Firebase.firestore }

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
        setRecycler()

        binding.fabCreateAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_serviceFragment)
        }
    }

    private fun setRecycler() {
        mAdapter = RecyclerAppointmentAdapter(appointments, this)
        binding.rvAppointments.apply {
            adapter = mAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
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