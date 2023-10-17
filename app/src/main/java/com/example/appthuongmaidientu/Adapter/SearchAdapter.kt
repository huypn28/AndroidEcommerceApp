package com.example.appthuongmaidientu.Adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appthuongmaidientu.Data.Product
import com.example.appthuongmaidientu.Helper.getProductPrice
import com.example.appthuongmaidientu.databinding.RecyclerviewSearchItemBinding

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    var onItemClick: ((Product) -> Unit)? = null


    inner class SearchViewHolder(val binding: RecyclerviewSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun  bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProductSearch)
                val priceAfterOffer=product.offerPercentage.getProductPrice(product.price)
                tvNewPriceSearch.text="${String.format("%.0f", priceAfterOffer)} VNĐ"
                tvPriceSearch.paintFlags= Paint.STRIKE_THRU_TEXT_FLAG

                if (product.offerPercentage==null)
                    tvNewPriceSearch.visibility= View.INVISIBLE
                tvPriceSearch.text="${String.format("%.0f", product.price)} VNĐ"
                tvNameSearch.text=product.name
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            RecyclerviewSearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.bind((product))
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}