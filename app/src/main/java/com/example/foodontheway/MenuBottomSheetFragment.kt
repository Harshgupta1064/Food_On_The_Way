package com.example.foodontheway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodontheway.adapter.MenuAdapter
import com.example.foodontheway.databinding.FragmentMenuBottomSheetBinding
import com.example.foodontheway.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

        binding.backButton.setOnClickListener(){
            dismiss()
        }

        retrieveMenuItems()
        return binding.root
    }

    private fun retrieveMenuItems() {
        database=FirebaseDatabase.getInstance()
        val foodRef :DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
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
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter =adapter
    }


    companion object {

    }
}