package com.example.foodontheway.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
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

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodItemIDs: MutableList<String>
    private lateinit var foodImageUri: MutableList<String>
    private lateinit var quantities: MutableList<Int>
    private lateinit var adapter: CartAdapter
    private lateinit var userId: String
    private lateinit var cartItemsToAdd: MutableList<CartItem?>
    private lateinit var totalAmount: String
    private lateinit var orderIdRef: DatabaseReference

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


        orderIdRef = database.reference.child("user").child(userId).child("cartItems")
        orderIdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Calculate total amount
                val totalAmount = calculateTotalAmount(snapshot)

                // Update the "Proceed" button text with the new total amount
                updateProceedButton(totalAmount)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })



        binding.proceedButton.setOnClickListener {
            if (binding.cartRecyclerView.isNotEmpty()) {
                getOrderItemDetails()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Add Your Favourite food to your Cart",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.root
    }

    private fun calculateTotalAmount(snapshot: DataSnapshot): Int {
        var totalAmount = 0
        for (cartSnapshot in snapshot.children) {
            val foodPrice =
                cartSnapshot.child("foodPrice").getValue(String::class.java)?.toIntOrNull() ?: 0
            val foodQuantity = cartSnapshot.child("foodQuantity").getValue(Int::class.java) ?: 0
            totalAmount += foodPrice * foodQuantity
        }
        return totalAmount
    }

    private fun updateProceedButton(totalAmount: Int) {
        // Update the text of the proceedButton to display the total amount
        binding.proceedButton.text = "Place Order Rs. $totalAmount"
    }


    private fun getOrderItemDetails() {
        orderIdRef = database.reference.child("user").child(userId).child("cartItems")
        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodImages = mutableListOf<String>()
        val foodDescriptions = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()
        val foodQuantities = mutableListOf<Int>()
        val foodItemIDs = mutableListOf<String>()


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
                    cartItems?.foodItemID?.let { foodItemIDs.add(it) }

                }
                orderNow(
                    foodNames,
                    foodPrices,
                    foodImages,
                    foodDescriptions,
                    foodIngredients,
                    foodQuantities,
                    foodItemIDs
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
        foodQuantities: MutableList<Int>,
        foodItemIDs: MutableList<String>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            intent.putExtra("orderFoodNames", foodNames as ArrayList<String>)
            intent.putExtra("orderFoodPrices", foodPrices as ArrayList<String>)
            intent.putExtra("orderFoodDescriptions", foodDescriptions as ArrayList<String>)
            intent.putExtra("orderFoodIngredients", foodIngredients as ArrayList<String>)
            intent.putExtra("orderFoodImages", foodImages as ArrayList<String>)
            intent.putExtra("foodItemIDs", foodItemIDs as ArrayList<String>)
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
        foodItemIDs = mutableListOf()
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
                    cartItem?.foodItemID?.let { foodItemIDs.add(it) }
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
