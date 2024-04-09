package com.example.foodontheway

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodontheway.databinding.ActivitySignupPageBinding
import com.example.foodontheway.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignupPage : AppCompatActivity() {
    private lateinit var userName:String
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClient:GoogleSignInClient

    private val binding: ActivitySignupPageBinding by lazy {
        ActivitySignupPageBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        setContentView(binding.root)
        auth = Firebase.auth
        database = Firebase.database.reference
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        binding.createAccount.setOnClickListener{
            userName = binding.userName.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(email.isBlank()||userName.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please fill all the Details", Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(email,password)
            }

        }
        binding.haveAccount.setOnClickListener{
            val intent = Intent(this,LoginPage::class.java)
            startActivity(intent)
        }
        binding.GButton.setOnClickListener{
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }


    }

    //Launcher for Google Sign In
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            //successfully signed in with google
                            Toast.makeText(
                                this,
                                "Successfully Signed in with Google",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Google Sign in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Google Sign in failed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
          task->
            if(task.isSuccessful){
                Toast.makeText(this, "Your Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Create Account","Account Creation Failed",task.exception)
            }

        }

    }

    private fun saveUserData() {
        val user:UserModel = UserModel(userName,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //save data to Firebase Database
        database.child("user").child(userId).setValue(user)
    }
}