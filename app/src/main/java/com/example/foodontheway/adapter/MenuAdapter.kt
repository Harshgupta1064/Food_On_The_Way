package com.example.foodontheway.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodontheway.DetailsActivity
import com.example.foodontheway.databinding.MenuItemBinding
import com.example.foodontheway.model.CartItem
import com.example.foodontheway.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuFoodName", menuItem.foodName)
                putExtra("menuFoodPrice", menuItem.foodPrice)
                putExtra("menuFoodDescription", menuItem.foodDescription)
                putExtra("menuFoodIngredients", menuItem.foodIngredients)
                putExtra("menuFoodImageUrl", menuItem.foodImage)
            }
            requireContext.startActivity(intent)

        }

        fun bind(position: Int) {

            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuFoodPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuFoodImage)
            }
            binding.menuAddToCart.setOnClickListener{
                addItemToCart()
            }




        }

        private fun addItemToCart() {
            val auth = FirebaseAuth.getInstance()
            val database=FirebaseDatabase.getInstance()
            val databaseRef = FirebaseDatabase.getInstance().reference
            val userId = auth.currentUser?.uid ?: ""
            val menuItem=menuItems[position]
            //create a CartItem Object
            val cartItem = CartItem(
                menuItem.foodName.toString(),
                menuItem.foodPrice.toString(),
                menuItem.foodDescription.toString(),
                menuItem.foodImage.toString(),
                1
            )

            database.reference.child("user").child(userId).child("cartItems").push().setValue(cartItem)
                .addOnSuccessListener {
                    Toast.makeText(requireContext, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext, "Item Not Added", Toast.LENGTH_SHORT).show()
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







