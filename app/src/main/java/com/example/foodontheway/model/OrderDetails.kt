package com.example.foodontheway.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class OrderDetails():Parcelable{
    var userId:String?=null
    var userName:String?=null
    var address:String?=null
    var totalAmount:Int?=null
    var phone:String?=null
    var orderAccepted:Boolean=false
    var paymentReceived:Boolean=false
    var itemPushKey:String?=null
    var orderTime:Long=0
    var FoodNames:MutableList<String>?=null
    var FoodPrices:MutableList<String>?=null
    var FoodDescription:MutableList<String>?=null
    var FoodImages:MutableList<String>?=null
    var FoodQuantities:MutableList<Int>?=null

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        phone = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        orderTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        address: String,
        totalOrderAmount: Int,
        phone: String,
        orderAccepted: Boolean,
        paymentReceived: Boolean,
        itemPushKey: String?,
        time: Long,
        foodNames: ArrayList<String>,
        foodPrices: ArrayList<String>,
        foodDescriptions: ArrayList<String>,
        foodImages: ArrayList<String>,
        foodQuantities: ArrayList<Int>
    ) : this(){
        this.userId=userId
        this.userName=name
        this.address=address
        this.totalAmount=totalOrderAmount
        this.phone=phone
        this.orderAccepted=orderAccepted
        this.paymentReceived=paymentReceived
        this.itemPushKey=itemPushKey
        this.orderTime=time
        this.FoodNames=foodNames
        this.FoodPrices=foodPrices
        this.FoodDescription=foodDescriptions
        this.FoodImages=foodImages
        this.FoodQuantities=foodQuantities

    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(orderTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }


}
