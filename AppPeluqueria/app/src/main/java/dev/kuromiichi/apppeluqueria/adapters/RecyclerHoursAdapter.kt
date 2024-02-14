package dev.kuromiichi.apppeluqueria.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kuromiichi.apppeluqueria.R
import dev.kuromiichi.apppeluqueria.databinding.ItemHourBinding
import dev.kuromiichi.apppeluqueria.listeners.HourOnClickListener

class RecyclerHoursAdapter(
    private var hours: List<String>,
    private val hourOnClickListener: HourOnClickListener
) : RecyclerView.Adapter<RecyclerHoursAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemHourBinding.bind(view)

        fun bind(hour: String) {
            binding.tvHour.text = hour
        }

        fun setListener(hour: String) {
            binding.root.setOnClickListener {
                hourOnClickListener.onHourClick(hour)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_hour,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hours[position])
        holder.setListener(hours[position])
    }

    fun setHours(hoursNew: List<String>) {
        hours = hoursNew
        notifyDataSetChanged()
    }
}