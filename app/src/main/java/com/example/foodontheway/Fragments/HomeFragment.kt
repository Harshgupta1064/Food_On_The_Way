package com.example.foodontheway.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodontheway.MenuBottomSheetFragment
import com.example.foodontheway.R
import com.example.foodontheway.adapter.MenuAdapter
import com.example.foodontheway.databinding.FragmentHomeBinding
import com.example.foodontheway.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var menuItems: MutableList<MenuItem>
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.ViewMenu.setOnClickListener() {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

//        retrieve and display Popular Items
        retrieveAndDisplayPopularItems()
        return binding.root


    }

    private fun retrieveAndDisplayPopularItems() {
        //get ref to database
        database=FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        menuItems = mutableListOf()

        //get data from database
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {menuItems.add(it)}
                }
                //display a random Popular item
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun randomPopularItems() {
        //create a shuffled list of menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItem=index.take(numItemToShow).map { menuItems[it] }

        setPopularItemAdapter(subsetMenuItem)
    }

    private fun setPopularItemAdapter(subsetMenuItem: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItem,requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.b2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.b3, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.b4, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)


        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected item $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }


        })



    }

    companion object {

    }
}