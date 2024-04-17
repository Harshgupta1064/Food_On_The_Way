package com.example.foodontheway.model

data class MenuItem(
    val foodDescription:String?=null,
    val foodImage:String?=null,
    val foodIngredients:String?=null,
    val foodItemID:String?=null,
    val foodName:String?=null,
    val foodPrice:String?=null,
){
    init {
        // Constructor body
    }
}
