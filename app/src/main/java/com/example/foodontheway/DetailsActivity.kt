package com.example.foodontheway

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodontheway.databinding.ActivityDetailsBinding
import com.example.foodontheway.model.CartItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
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


    }

    private fun addItemToCart() {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""

        //create a CartItem Object
        val cartItem = CartItem(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            1
        )

        database.reference.child("user").child(userId).child("cartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Item Not Added", Toast.LENGTH_SHORT).show()
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