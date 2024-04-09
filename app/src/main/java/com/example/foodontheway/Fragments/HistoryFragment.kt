package com.example.foodontheway.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodontheway.adapter.BuyAgainAdapter
import com.example.foodontheway.adapter.CartAdapter
import com.example.foodontheway.databinding.FragmentHistoryBinding
import com.example.foodontheway.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: BuyAgainAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private var listOfOrderItems: MutableList<OrderDetails>   = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        //Initialize auth and database
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        //retrieve and display order History
        retrieveAndDisplayOrderDetails()
        listOfOrderItems.reverse()
        if(listOfOrderItems.isNotEmpty()){
            setDataInRecentBuyItem()
            setPreviousBuyItemRecyclerView()
        }


        return binding.root
    }

    private fun retrieveAndDisplayOrderDetails() {
        binding.RecentOrder.visibility = View.INVISIBLE
        userId=auth.currentUser?.uid?:""
        val orderDetailsRef=database.reference.child("user").child(userId).child("Buy History")
        val sortQuery = orderDetailsRef.orderByChild("orderTime")

        sortQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children){
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems?.add(it)
                    }
                }

            listOfOrderItems.reverse()
            if(listOfOrderItems.isNotEmpty()){
                setDataInRecentBuyItem()
                setPreviousBuyItemRecyclerView()
            }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setDataInRecentBuyItem() {
        binding.RecentOrder.visibility=View.VISIBLE
        val recentOrderItem=listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding){
                buyAgainFoodName.text=recentOrderItem.FoodNames?.firstOrNull()?:""
                buyAgainFoodPrice.text=recentOrderItem.FoodPrices?.firstOrNull()?:""
                val image = recentOrderItem.FoodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainFoodImage)
                isRecievedButton.setOnClickListener{
                    val updates = HashMap<String, Any>()
                    updates["orderAccepted"] = true
                    updates["paymentReceived"] = true
                    val orderRef=database.reference.child("user").child(userId).child("OrderDetails")
                        .child(recentOrderItem.itemPushKey!!).updateChildren(updates)
                    Toast.makeText(requireContext(), "We Hope you like the food and Delivery Experience", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun setPreviousBuyItemRecyclerView() {
        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodImages = mutableListOf<String>()

        for (orderDetails in listOfOrderItems) {
            orderDetails.FoodNames?.let { foodNames.addAll(it) }
            orderDetails.FoodPrices?.let { foodPrices.addAll(it) }
            orderDetails.FoodImages?.let { foodImages.addAll(it) }
        }

        setAdapter(foodNames, foodPrices, foodImages)
    }


    private fun setAdapter(
        foodNames: MutableList<String>,
        foodPrices: MutableList<String>,
        foodImages: MutableList<String>
    ) {
        val  adapter=BuyAgainAdapter(foodNames,foodPrices,foodImages,requireContext())
        binding.buyAgainRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.buyAgainRecyclerView.adapter=adapter
    }

}