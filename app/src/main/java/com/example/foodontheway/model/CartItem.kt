package com.example.foodontheway.model

data class CartItem(
    var foodName:String?=null,
    var foodPrice:String?=null,
    var foodDescription:String?=null,
    var foodImage:String?=null,
    var foodQuantity:Int?=null,
    var foodIngredient:String?=null,
    var foodItemID:String?=null,
)