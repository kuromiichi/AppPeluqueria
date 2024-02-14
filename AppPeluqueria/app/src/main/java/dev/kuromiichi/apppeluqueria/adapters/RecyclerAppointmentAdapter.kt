package dev.kuromiichi.apppeluqueria.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.ItemAppointmentBinding
import dev.kuromiichi.apppeluqueria.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueria.models.Appointment

class RecyclerAppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onClickListener: AppointmentOnClickListener
) : RecyclerView.Adapter<RecyclerAppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAppointmentBinding.bind(view)

        fun bind(appointment: Appointment) {
            binding.tvAppointmentServices.text = "Servicios:\n"
            appointment.services.forEach {
                binding.tvAppointmentServices.append(it.name + "\n")
            }
            binding.tvAppointmentDateTime.text = appointment.date + " " + appointment.time
            binding.tvAppointmentDuration.text = appointment.duration.toString() + " min"
        }

        fun setListener(appointment: Appointment) {
            binding.root.setOnClickListener {
                onClickListener.onAppointmentClick(appointment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_appointment,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
        holder.setListener(appointments[position])
    }

    fun setAppointments(appointments: List<Appointment>) {
        this.appointments = appointments
        notifyDataSetChanged()
    }
}