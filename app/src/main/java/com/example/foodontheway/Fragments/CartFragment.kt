package com.example.foodontheway.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodontheway.PayoutActivity
import com.example.foodontheway.adapter.CartAdapter
import com.example.foodontheway.databinding.FragmentCartBinding
import com.example.foodontheway.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodImageUri: MutableList<String>
    private lateinit var quantities: MutableList<Int>
    private lateinit var adapter: CartAdapter
    private lateinit var userId: String
    private lateinit var cartItemsToAdd: MutableList<CartItem?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveCartItems()
        binding.proceedButton.setOnClickListener {
            //get order Item details
            getOrderItemDetails()

        }
        return binding.root
    }


    private fun getOrderItemDetails() {
        val orderIdRef = database.reference.child("user").child(userId).child("cartItems")
        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodImages = mutableListOf<String>()
        val foodDescriptions = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()
        val foodQuantities= mutableListOf<Int>()

        orderIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItems = foodSnapshot.getValue(CartItem::class.java)
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodImage?.let { foodImages.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }
                    cartItems?.foodQuantity?.let { foodQuantities.add(it) }

                }
                orderNow(
                    foodNames,
                    foodPrices,
                    foodImages,
                    foodDescriptions,
                    foodIngredients,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun orderNow(
        foodNames: MutableList<String>,
        foodPrices: MutableList<String>,
        foodImages: MutableList<String>,
        foodDescriptions: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            intent.putExtra("orderFoodNames", foodNames as ArrayList<String>)
            intent.putExtra("orderFoodPrices", foodPrices as ArrayList<String>)
            intent.putExtra("orderFoodDescriptions", foodDescriptions as ArrayList<String>)
            intent.putExtra("orderFoodIngredients", foodIngredients as ArrayList<String>)
            intent.putExtra("orderFoodImages", foodImages as ArrayList<String>)
            intent.putExtra("orderFoodQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }


    private fun retrieveCartItems() {
        userId = auth.currentUser?.uid ?: ""
        val foodRef: DatabaseReference =
            database.reference.child("user").child(userId).child("cartItems")
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodIngredients = mutableListOf()
        foodImageUri = mutableListOf()
        quantities = mutableListOf()
        cartItemsToAdd = mutableListOf()
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItem::class.java)
                    cartItem?.foodName?.let { foodNames.add(it) }
                    cartItem?.foodPrice?.let { foodPrices.add(it) }
                    cartItem?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItem?.foodImage?.let { foodImageUri.add(it) }
                    cartItem?.foodIngredient?.let { foodIngredients.add(it) }
                    cartItem?.foodQuantity?.let { quantities.add(it) }
                    cartItemsToAdd.add(cartItem)
                }
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setAdapter() {
        adapter = CartAdapter(cartItemsToAdd, requireContext())
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter
    }


}
