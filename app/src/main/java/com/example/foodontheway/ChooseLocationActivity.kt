package com.example.foodontheway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.foodontheway.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding:ActivityChooseLocationBinding by lazy{
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = arrayOf("Gwalior","Jaipur","Delhi","Mumbai","Chandigarh","Indore","Bhopal","Jodhpur","Guru gram","Agra","Mathura","Jhansi","Itarsi")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.locationList
        autoCompleteTextView.setAdapter(adapter)
    }
}