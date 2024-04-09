package com.example.foodontheway.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodontheway.R
import com.example.foodontheway.adapter.MenuAdapter
import com.example.foodontheway.databinding.FragmentSearchBinding
import com.example.foodontheway.model.MenuItem
import com.google.android.material.search.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        retrieveMenuItems()
        return binding.root
    }

    private fun retrieveMenuItems() {
        database= FirebaseDatabase.getInstance()
        val foodRef : DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {menuItems.add(it)}
                }
                //Set Adapter
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun setAdapter() {
        val adapter = MenuAdapter(menuItems,requireContext())
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter =adapter
    }


    companion object {

    }
}