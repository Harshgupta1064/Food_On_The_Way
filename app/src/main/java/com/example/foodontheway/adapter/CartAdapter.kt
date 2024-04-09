package com.example.foodontheway.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodontheway.databinding.CartItemBinding
import com.example.foodontheway.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class CartAdapter(
    private val cartItems: MutableList<CartItem?>,
    private val requireContext: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var uniqueKey:String?=null

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size
        itemQuantities = IntArray(cartItems.size) { 1 }
        cartItemRef = database.reference.child("user").child(userId).child("cartItems")
    }

    companion object {

        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemRef: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()

        for(item in cartItems){
            item?.foodQuantity?.let { itemQuantity.add(it)}
        }
            return itemQuantity
    }


    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val cartItem = cartItems[position]
            binding.apply {



                val quantity = itemQuantities[position]
                itemName.text = cartItem?.foodName
                itemPrice.text = cartItem?.foodPrice
                val uri = Uri.parse(cartItem?.foodImage)
                Glide.with(requireContext).load(uri).into(itemImage)



                plusButton.setOnClickListener() {
                    increaseQuantity(position)
                }
                minusButton.setOnClickListener() {
                    decreaseQuantity(position)
                }
                deleteButton.setOnClickListener() {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteQuantity(position)
                    }
                }
                getUniqueKeyAtPosition(position) { uniqueKey ->
                    if (uniqueKey != null) {
                        cartItemRef.child(uniqueKey).child("foodQuantity")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val foodQuantity = dataSnapshot.getValue(Int::class.java)
                                    // Now you can use foodQuantity as needed
                                    binding.cartItemQuantity.text = foodQuantity.toString()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle potential errors here
                                }
                            })

                    }
                }

            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartItems[position]?.foodQuantity= itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
                getUniqueKeyAtPosition(position) { uniqueKey ->
                    if (uniqueKey != null) {
                        val updates = HashMap<String, Any>()
                        updates["foodQuantity"] = itemQuantities[position]
                        cartItemRef.child(uniqueKey).updateChildren(updates)
                    }
                }

            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartItems[position]?.foodQuantity= itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
                getUniqueKeyAtPosition(position) { uniqueKey ->
                    if (uniqueKey != null) {
                        val updates = HashMap<String, Any>()
                        updates["foodQuantity"] = itemQuantities[position]
                        cartItemRef.child(uniqueKey).updateChildren(updates)
                    }
                }
            }
        }

        private fun deleteQuantity(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)

                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemRef.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItems.removeAt(position)
                    //update Item quantity
                    itemQuantities =
                        itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    Toast.makeText(requireContext, "Item Removed Successfully", Toast.LENGTH_SHORT)
                        .show()

                }
                    .addOnFailureListener {
                        Toast.makeText(requireContext, "Failed to remove", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrieve) {
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
