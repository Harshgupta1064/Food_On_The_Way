package com.example.foodontheway.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodontheway.databinding.FragmentProfileBinding
import com.example.foodontheway.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentProfileBinding.inflate(inflater, container, false)
        setUserData()
        binding.saveButton.setOnClickListener {
            val name = binding.nameProfile.text.toString()
            val email = binding.emailProfile.text.toString()
            val address = binding.addressProfile.text.toString()
            val phone = binding.phoneProfile.text.toString()

            saveUserData(name, email, address, phone)

        }
        return binding.root
    }

    private fun saveUserData(
        userName: String,
        userEmail: String,
        userAddress: String,
        userPhone: String
    ) {
        val userId = auth.currentUser?.uid ?: ""
        val userRef = database.getReference("user").child(userId)
        if (userId != null) {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = hashMapOf(
                        "userName" to userName,
                        "email" to userEmail,
                        "address" to userAddress,
                        "phone" to userPhone
                    )
                    userRef.setValue(userData).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Information Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Information Update Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid ?: ""
        val userRef = database.reference.child("user").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userProfile = snapshot.getValue(UserModel::class.java)
                    if (userProfile != null) {
                        binding.nameProfile.setText(userProfile.userName)
                        binding.emailProfile.setText(userProfile.email)
                        binding.addressProfile.setText(userProfile.address)
                        binding.phoneProfile.setText(userProfile.phone)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {

    }
}