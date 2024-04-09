package com.example.foodontheway.model

data class MenuItem(
    val foodName:String?=null,
    val foodPrice:String?=null,
    val foodDescription:String?=null,
    val foodImage:String?=null,
    val foodIngredients:String?=null,
){
    init {
        // Constructor body
    }
}
