package com.example.foodontheway.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodontheway.R
import com.example.foodontheway.adapter.NotificationAdapter
import com.example.foodontheway.databinding.FragmentNotificationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater,container,false)
        val notification = listOf("Your Order has been Cancelled Successfully","Your Order has been taken by Driver")
        val notificationImage = listOf(R.drawable.right, R.drawable.sademoji)
        adapter = NotificationAdapter(notification as MutableList<String>,notificationImage as MutableList<Int>)
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {

    }
}