package dev.kuromiichi.apppeluqueria.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.adapters.RecyclerHoursAdapter
import dev.kuromiichi.apppeluqueria.databinding.FragmentAppointmentBinding
import dev.kuromiichi.apppeluqueria.listeners.HourOnClickListener
import dev.kuromiichi.apppeluqueria.models.Appointment
import dev.kuromiichi.apppeluqueria.models.Service
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Date
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
    private var openDays = BooleanArray(7)
    private var availableHours = emptyList<String>()
    private var openingTime: Date? = null
    private var closingTime: Date? = null
    private var maxAppointments = 0
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
        getSettings()
        setRecycler()
        setButtons()
    }

    private fun getSettings() {
        db.collection("settings").document("settings").get()
            .addOnSuccessListener { result ->
                result.data?.let {
                    openDays = it["open_days"] as BooleanArray
                    openingTime = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).parse(it["opening_time"].toString())
                    closingTime = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).parse(it["closing_time"].toString())
                    maxAppointments = it["max_appointments"].toString().toInt()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_settings_error),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_appointmentFragment_to_homeFragment)
            }
    }

    private fun setRecycler() {
        adapter = RecyclerHoursAdapter(availableHours, this)
        binding.rvHours.apply {
            adapter = this@AppointmentFragment.adapter
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    private fun setButtons() {
        binding.fabConfirmAppointment.setOnClickListener {
            if (selectedHour == "") {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_no_hour_selected),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.dialog_confirm_appointment_title)
                setMessage(R.string.dialog_confirm_appointment_message)
                setPositiveButton(R.string.dialog_yes) { _, _ ->
                    confirmAppointment()
                }
                setNegativeButton(R.string.dialog_no) { _, _ -> }
            }
        }

        binding.imageButtonCalendar.setOnClickListener {
            datePickerDialog(binding.tilDate.editText!!)
        }
    }

    private fun confirmAppointment() {
        db.collection("appointments").add(
            Appointment(
                userUid = auth.uid.toString(),
                date = date,
                time = selectedHour,
                services = services
            )
        ).addOnSuccessListener { result ->
            db.collection("appointments").document(result.id)
                .update("id", result.id).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_confirm_appointment_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_appointmentFragment_to_homeFragment)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_confirm_appointment_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    db.collection("appointments").document(result.id).delete()
                }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_confirm_appointment_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun datePickerDialog(editText: EditText) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val thisMonth = MaterialDatePicker.thisMonthInUtcMilliseconds()

        val dayOfWeekValidator = object : DateValidator {
            override fun describeContents(): Int = 0

            override fun writeToParcel(dest: Parcel, flags: Int) {}

            override fun isValid(date: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date

                return openDays[calendar[Calendar.DAY_OF_WEEK] - 1]
            }
        }

        val dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.choose_date))
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setOpenAt(thisMonth)
                    .setStart(today)
                    .setValidator(dayOfWeekValidator)
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .build()
            ).build()
        dialog.show(requireActivity().supportFragmentManager, "DATE_PICKER")

        dialog.addOnPositiveButtonClickListener {
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
            editText.setText(date)
            setAvailableHours()
            updateRecycler()
        }
    }

    private fun setAvailableHours() {
        if (openingTime == null || closingTime == null) return

        val calendarOpening = Calendar.getInstance().apply { time = openingTime!! }
        val calendarClosing = Calendar.getInstance().apply { time = closingTime!! }

        // Ajustar inicio y fin de horas disponibles
        when (calendarOpening[MINUTE]) {
            in 1..30 -> calendarOpening[MINUTE] = 30
            in 31..59 -> {
                calendarOpening[HOUR_OF_DAY] += 1
                calendarOpening[MINUTE] = 0
            }
        }
        calendarClosing.add(MINUTE, -30)

        // Crear lista de horas disponibles sin filtrar
        val possibleHours = mutableListOf<Date>()
        while (calendarOpening.time < calendarClosing.time) {
            possibleHours.add(calendarOpening.time)
            calendarOpening.add(MINUTE, 30)
        }

        // Si la fecha ya ha pasado, no mostrar horas anteriores
        possibleHours.removeIf { hour ->
            hour.before(Calendar.getInstance().time)
        }

        // Quitar las horas posibles que ya estén reservadas
        var occupiedHours = emptyList<Date>()
        db.collection("appointments").get().addOnSuccessListener { result ->
            occupiedHours = result.toObjects(Appointment::class.java)
                .filter { it.date == date }
                .groupBy { it.time }
                .mapValues { it.value.size }
                .filter { it.value >= maxAppointments }
                .keys
                .map { SimpleDateFormat("HH:mm", Locale.getDefault()).parse(it)!! }

            occupiedHours.forEach { possibleHours.remove(it) }
        }

        // Quitar las horas sin suficiente tiempo disponible
        possibleHours.removeIf { hour ->
            getFreeMinutes(hour, occupiedHours) < services.sumOf { it.duration }
        }

        availableHours = possibleHours
            .map { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it) }
    }

    private fun getFreeMinutes(date: Date, occupiedHours: List<Date>): Int {
        val firstOccupied = occupiedHours.firstOrNull { it.after(date) } ?: closingTime

        return ((firstOccupied!!.time - date.time) / 1000 / 60).toInt()
    }

    private fun updateRecycler() {
        adapter.setHours(availableHours)
    }

    override fun onHourClick(hour: String) {
        selectedHour = hour
    }
}