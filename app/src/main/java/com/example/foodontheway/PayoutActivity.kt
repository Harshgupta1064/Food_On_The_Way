package com.example.foodontheway

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodontheway.Fragments.CartFragment
import com.example.foodontheway.databinding.ActivityPayoutBinding
import com.example.foodontheway.model.OrderDetails
import com.example.foodontheway.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayoutBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var address: String
    private lateinit var foodNames: ArrayList<String>
    private lateinit var foodPrices: ArrayList<String>
    private lateinit var foodImages: ArrayList<String>
    private lateinit var foodDescriptions: ArrayList<String>
    private lateinit var foodIngredients: ArrayList<String>
    private lateinit var foodQuantities: ArrayList<Int>
    private var totalOrderAmount: Int = 0
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialise auth and database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        var userRef = database.reference.child("user")


        //get user data from cart fragment by intent
        val intent = intent
        foodNames = intent.getStringArrayListExtra("orderFoodNames") ?: arrayListOf()
        foodPrices = intent.getStringArrayListExtra("orderFoodPrices") ?: arrayListOf()
        foodDescriptions = intent.getStringArrayListExtra("orderFoodDescriptions") ?: arrayListOf()
        foodImages = intent.getStringArrayListExtra("orderFoodImages") ?: arrayListOf()
        foodIngredients = intent.getStringArrayListExtra("orderFoodIngredients") ?: arrayListOf()
        foodQuantities = intent.getIntegerArrayListExtra("orderFoodQuantities") as ArrayList<Int>
        totalOrderAmount = calculateTotalAmount()
        binding.totalAmount.text = totalOrderAmount.toString()

        setUserData()
        binding.placeOrderButton.setOnClickListener {
            name = binding.nameProfile.text.toString().trim()
            address = binding.addressProfile.text.toString().trim()
            phone = binding.phoneProfile.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }


        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey =
            database.reference.child("user").child(userId).child("orderDetails").push().key
        val orderDetails = OrderDetails(
            userId,
            name,
            address,
            totalOrderAmount,
            phone,
            false,
            false,
            itemPushKey,
            time,
            foodNames,
            foodPrices,
            foodDescriptions,
            foodImages,
            foodQuantities
        )
        val orderReference = database.reference.child("user").child(userId).child("OrderDetails")
            .child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails): Unit {
        database.reference.child("user").child(userId).child("Buy History")
            .child(orderDetails.itemPushKey!!).setValue(orderDetails).addOnSuccessListener {
            Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Order Not Placed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeItemFromCart(): Unit {
        val cartRef = database.reference.child("user").child(userId).child("cartItems")
        cartRef.removeValue()
    }


    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodPrices.size) {
            totalAmount = totalAmount + foodPrices[i].toInt() * foodQuantities[i]
        }
        return totalAmount

    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid ?: ""
        val userRef = database.reference.child("user").child(userId)
        val totalAmount: Int = 0
        userRef.child("cartItems").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    dataSnapshot
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userProfile = snapshot.getValue(UserModel::class.java)
                    if (userProfile != null) {
                        binding.nameProfile.setText(userProfile.userName)
                        binding.phoneProfile.setText(userProfile.phone)
                        binding.addressProfile.setText(userProfile.address)
                    }
                }

            }


            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}