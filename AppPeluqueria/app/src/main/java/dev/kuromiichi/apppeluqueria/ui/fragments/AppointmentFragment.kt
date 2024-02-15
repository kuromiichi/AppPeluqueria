package dev.kuromiichi.apppeluqueria.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.adapters.RecyclerHoursAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentAppointmentBinding
import dev.kuromiichi.apppeluqueria.listeners.HourOnClickListener
import dev.kuromiichi.apppeluqueria.models.Service
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentFragment : Fragment(), HourOnClickListener {
    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    private val args by navArgs<AppointmentFragmentArgs>()
    private var services = emptyList<Service>()

    private lateinit var adapter: RecyclerHoursAdapter

    private var date = ""
    private var hours = emptyList<String>()
    private var limitHours = emptyList<String>()
    private var selectedHour = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        services = args.services.toList()
        setRecycler()
        setListeners()
    }

    private fun setRecycler() {
        adapter = RecyclerHoursAdapter(hours, this)
        binding.rvHours.apply {
            adapter = this@AppointmentFragment.adapter
            layoutManager = GridLayoutManager(context, 4)
        }
        getLimitHours()
        setHoursList()
        updateRecycler()
    }

    private fun updateRecycler() {
        // TODO Con las horas disponibles, actualiza el recycler
    }

    private fun setHoursList() {
        // TODO Con las horas de la pelu, crea una lista de horas posibles
        // TODO Ten en cuenta que las horas no aparezcan
        // TODO si ya hay máximo de reservas en la duracion de la cita
    }

    private fun getLimitHours() {
        // TODO Coge las horas de abridura y cerradura de la pelu
    }

    override fun onHourClick(hour: String) {
        selectedHour = hour
        // TODO Se puede cambiar el color del item si está seleccionado?
    }

    private fun setListeners() {
        binding.fabConfirmAppointment.setOnClickListener {
            // TODO Dialogo para confirmar la cita, y luego la cosa esa
            // TODO para que no se pueda volver atras despues de confirmar e ir al home fragment
        }

        binding.imageButtonCalendar.setOnClickListener {
            datePickerDialog(binding.tilDate.editText!!)
        }
    }

    private fun datePickerDialog(textInput: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                textInput.setText(
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(calendar.time)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}