package com.example.foodontheway.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodontheway.DetailsActivity
import com.example.foodontheway.databinding.MenuItemBinding
import com.example.foodontheway.model.CartItem
import com.example.foodontheway.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MenuAdapter(
    private var menuItems: List<MenuItem>,
    private var requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private lateinit var cartItemRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var count = 0

    init {
        var auth = FirebaseAuth.getInstance()
        var userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance()
        cartItemRef = database.reference.child("user").child(userId).child("cartItems")
        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                count = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching data: $error")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        var binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var auth = FirebaseAuth.getInstance()
        var userId = auth.currentUser?.uid ?: ""
        var database = FirebaseDatabase.getInstance()

        init {
            binding.root.setOnClickListener {
                var position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            var menuItem = menuItems[position]
            var intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuFoodName", menuItem.foodName)
                putExtra("menuFoodPrice", menuItem.foodPrice)
                putExtra("menuFoodDescription", menuItem.foodDescription)
                putExtra("menuFoodIngredients", menuItem.foodIngredients)
                putExtra("menuFoodImageUrl", menuItem.foodImage)
                putExtra("menuFoodItemID", menuItem.foodItemID)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            var menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuFoodPrice.text = menuItem.foodPrice
                Glide.with(requireContext).load(Uri.parse(menuItem.foodImage)).into(menuFoodImage)
            }


            val itemKey: String? = menuItem.foodItemID
            val cartRef = database.reference.child("user").child(userId).child("cartItems")
            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isItemInCart = false
                    var itemKeyInCart: String? = null
                    for (item in snapshot.children) {
                        if (itemKey == item.child("foodItemID").getValue(String::class.java)) {
                            isItemInCart = true
                            itemKeyInCart = item.key // Save the key of the item in cart
                            break
                        }
                    }
                    if (isItemInCart) {
                        // Item is already in cart, show quantity selector
                        binding.menuAddToCart.visibility = View.GONE
                        binding.quantitySelector.visibility = View.VISIBLE
                        val itemQuantity = snapshot.child(itemKeyInCart!!).child("foodQuantity")
                            .getValue(Int::class.java)
                        binding.quantity.text = itemQuantity.toString()
                        binding.plusButton.setOnClickListener {
                            increaseQuantity( itemKeyInCart)
                        }
                        binding.minusButton.setOnClickListener {
                            decreaseQuantity( itemKeyInCart)
                        }
                    } else {
                        binding.menuAddToCart.setOnClickListener {
                            // Item is not in cart, add it
                            addItemToCart(menuItem)
                            // Update UI
                            binding.menuAddToCart.visibility = View.GONE
                            binding.quantitySelector.visibility = View.VISIBLE

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext, "Database Error", Toast.LENGTH_SHORT).show()
                }
            })


        }

        private fun addItemToCart(menuItem: MenuItem) {
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: ""
            val cartItem = CartItem(
                menuItem.foodName,
                menuItem.foodPrice,
                menuItem.foodDescription,
                menuItem.foodImage,
                1,
                menuItem.foodIngredients,
                menuItem.foodItemID
            )

            val cartItemRef = database.reference.child("user").child(userId).child("cartItems").push()
            var cartItemId:String?=null
            cartItemRef.setValue(cartItem)
                .addOnSuccessListener {
                    cartItemId = cartItemRef.key // Retrieve the key of the newly pushed item
                    Toast.makeText(requireContext, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                    // Now you can use cartItemId as needed
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext, "Item Not Added", Toast.LENGTH_SHORT).show()
                }

            binding.plusButton.setOnClickListener {
                increaseQuantity(cartItemId!!)
            }
            binding.minusButton.setOnClickListener {
                decreaseQuantity( cartItemId!!)
            }
        }

        private fun increaseQuantity( itemKey: String) {
            database.reference.child("user").child(userId).child("cartItems").child(itemKey)
                .child("foodQuantity").get().addOnSuccessListener { dataSnapshot ->
                    val currentQuantity = dataSnapshot.getValue(Int::class.java) ?: 0
                    val updatedQuantity = currentQuantity + 1
                    binding.quantity.text = updatedQuantity.toString()
                    // Update the quantity in the database
                    database.reference.child("user").child(userId).child("cartItems").child(itemKey)
                        .child("foodQuantity").setValue(updatedQuantity)
                }
        }

        private fun decreaseQuantity( itemKey: String) {
            database.reference.child("user").child(userId).child("cartItems").child(itemKey)
                .child("foodQuantity").get().addOnSuccessListener { dataSnapshot ->
                    val currentQuantity = dataSnapshot.getValue(Int::class.java) ?: 0
                    if (currentQuantity > 1) {
                        val updatedQuantity = currentQuantity - 1
                        binding.quantity.text = updatedQuantity.toString()
                        // Update the quantity in the database
                        database.reference.child("user").child(userId).child("cartItems").child(itemKey)
                            .child("foodQuantity").setValue(updatedQuantity)
                    }
                }
        }

    }
}
