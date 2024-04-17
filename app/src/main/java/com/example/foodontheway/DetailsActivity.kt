package com.example.foodontheway

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodontheway.databinding.ActivityDetailsBinding
import com.example.foodontheway.model.CartItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
    private var foodItemID: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        foodName = intent.getStringExtra("menuFoodName")
        foodImage = intent.getStringExtra("menuFoodImageUrl")
        foodPrice = intent.getStringExtra("menuFoodPrice")
        foodDescription = intent.getStringExtra("menuFoodDescription")
        foodIngredients = intent.getStringExtra("menuFoodIngredients")
        foodItemID = intent.getStringExtra("menuFoodItemID")
        binding.foodName.text = foodName
        binding.Description.text = foodDescription
        binding.Ingredients.text = foodIngredients
        if (foodImage != null) {
            val uri = Uri.parse(foodImage)
            Glide.with(this).load(uri).into(binding.foodImage)
        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.AddToCartButton.setOnClickListener {
            addItemToCart()

        }
        val itemKey: String? = foodItemID
        val userId = auth.currentUser?.uid?:""
        val cartRef = database.reference.child("user").child(userId).child("cartItems")
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isItemInCart = false
                var itemKeyInCart: String?=""
                for (item in snapshot.children) {
                    if (itemKey == item.child("foodItemID").getValue(String::class.java)) {
                        isItemInCart = true
                        itemKeyInCart = item.key // Save the key of the item in cart
                        break
                    }
                }
                if (isItemInCart) {
                    // Item is already in cart, show quantity selector
                    binding.AddToCartButton.visibility = View.GONE
                    binding.quantitySelector.visibility = View.VISIBLE
                    val itemQuantity = snapshot.child(itemKeyInCart!!).child("foodQuantity")
                        .getValue(Int::class.java)
                    binding.quantity.text = itemQuantity.toString()
                    binding.plusButton.setOnClickListener {
                        increaseQuantity(itemKeyInCart)
                    }
                    binding.minusButton.setOnClickListener {
                        decreaseQuantity( itemKeyInCart)
                    }
                } else {
                    binding.AddToCartButton.setOnClickListener {
                        // Item is not in cart, add it
                        addItemToCart()
                        // Update UI
                        binding.AddToCartButton.visibility = View.GONE
                        binding.quantitySelector.visibility = View.VISIBLE
                        val itemQuantity=1
                        binding.quantity.text=itemQuantity.toString()

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Database Error", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun addItemToCart(){
        val userId = auth.currentUser?.uid ?: ""
        //create a CartItem Object
        val cartItem = CartItem(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            1,
            foodItemID
        )
        val cartItemRef = database.reference.child("user").child(userId).child("cartItems").push()
        var cartItemId:String?=null
        cartItemRef.setValue(cartItem)
            .addOnSuccessListener {
                cartItemId = cartItemRef.key // Retrieve the key of the newly pushed item
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                // Now you can use cartItemId as needed
            }
            .addOnFailureListener {
                Toast.makeText(this, "Item Not Added", Toast.LENGTH_SHORT).show()
            }


        binding.plusButton.setOnClickListener {
                increaseQuantity(cartItemId!!)
        }
        binding.minusButton.setOnClickListener {
                decreaseQuantity( cartItemId!!)
        }




    }
    private fun increaseQuantity( itemKey: String) {
        val userId = auth.currentUser?.uid ?: ""
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
        val userId : String = auth.currentUser?.uid?: ""
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