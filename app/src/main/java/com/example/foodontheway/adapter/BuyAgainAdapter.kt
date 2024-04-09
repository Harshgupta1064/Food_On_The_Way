package com.example.foodontheway.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodontheway.databinding.BuyAgainItemBinding
import com.example.foodontheway.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BuyAgainAdapter(private val itemName : MutableList<String>,private val itemPrice : MutableList<String>,private val itemImage : MutableList<String>,private val context:Context) : RecyclerView.Adapter<BuyAgainAdapter.buyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): buyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return buyAgainViewHolder(binding)
    }


    override fun onBindViewHolder(holder: buyAgainViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int {
        return itemName.size
    }
    inner class buyAgainViewHolder(private val binding : BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int
        ) {
            binding.apply {
                buyAgainFoodName.text = itemName[position]
                buyAgainFoodPrice.text = itemPrice[position]
                val imageUri = Uri.parse(itemImage[position])
                Glide.with(context).load(imageUri).into(buyAgainFoodImage)
                binding.buyAgainButton.setOnClickListener{
                    addItemToCart()
                }
            }
        }

        private fun addItemToCart() {
            val auth = FirebaseAuth.getInstance()
            val database=FirebaseDatabase.getInstance()
            val databaseRef = FirebaseDatabase.getInstance().reference
            val userId = auth.currentUser?.uid ?: ""
//            val buyAgainItem=itemName[position]
            //create a CartItem Object
            val cartItem = CartItem(
                foodName = itemName[position].toString(),
                foodPrice = itemPrice[position].toString(),
                foodImage = itemImage[position].toString(),
                foodQuantity = 1
            )

            database.reference.child("user").child(userId).child("cartItems").push().setValue(cartItem)
                .addOnSuccessListener {
                    Toast.makeText(context, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Item Not Added", Toast.LENGTH_SHORT).show()
                }


            //save data to the Id of the user
//        databaseRef.child("user").child(userId).child("CartItems").setValue(cartItem)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener{
//                Toast.makeText(this, "Item Not Added", Toast.LENGTH_SHORT).show()
//            }
        }



    }
}