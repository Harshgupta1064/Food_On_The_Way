package com.example.foodontheway.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodontheway.databinding.NotificationItemBinding

class NotificationAdapter(private val notificationText : MutableList<String>,private val notificationImage : MutableList<Int>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position,notificationText,notificationImage)
    }
    override fun getItemCount(): Int {
        return notificationText.size
    }
    class NotificationViewHolder(private val binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int,
            NotificationText: MutableList<String>,
            NotificationImage: MutableList<Int>
        ) {
            binding.apply {
                notificationText.text = NotificationText[position]
                notificationImage.setImageResource(NotificationImage[position])
            }

            }
        }

    }
