package com.example.foodontheway.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.car.ui.toolbar.MenuItem.OnClickListener
import com.example.foodontheway.DetailsActivity
import com.example.foodontheway.databinding.PopularItemBinding

class PopularAdapter (private val items : List<String>,private val prices:List <String>,private val images:List <Int>,private val requireContext: Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    private val itemClickListener : OnClickListener ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item=items[position]
        val image=images[position]
        val price = prices[position]
        holder.bind(item,price,image)
    }
    override fun getItemCount(): Int {
        return prices.size
    }




    inner class PopularViewHolder(private val binding : PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                //Set on click listeners to open details
                val intent = Intent(requireContext,DetailsActivity ::class.java)
                intent.putExtra("menuItemName",items.get(position))
                intent.putExtra("menuItemImage",images.get(position))
                requireContext.startActivity(intent)
            }
        }
        private val imageView = binding.FoodImagePopular
        fun bind(item: String,price: String, image: Int) {
            binding.FoodNamePopular.text = item
            binding.FoodPricePopular.text = price
            imageView.setImageResource(image)



        }

    }
interface OnClickListener{
    fun onItemClick(position: Int){

    }

}
}

