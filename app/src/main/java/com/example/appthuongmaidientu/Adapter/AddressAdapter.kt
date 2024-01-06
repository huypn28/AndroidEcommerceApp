package com.example.appthuongmaidientu.Adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.appthuongmaidientu.Data.Address
import com.example.appthuongmaidientu.EncryptionUtils
import com.example.appthuongmaidientu.R
import com.example.appthuongmaidientu.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder( val binding: AddressRvItemBinding):
        ViewHolder(binding.root){
            fun bind(address: Address,isSelected:Boolean){
                binding.apply {
                    // Lấy dữ liệu từ đối tượng address
                    val decryptedTitle = EncryptionUtils.decrypt(address.addressTiTle, "your_password")
                    buttonAddress.text = decryptedTitle
                    if (isSelected){
                        buttonAddress.background=ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                    }else{
                        buttonAddress.background=ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                    }

                }
            }

    }

    private val diffCallback=object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTiTle == newItem.addressTiTle && oldItem.fullName==newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    var selectedAddress=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address=differ.currentList[position]
        holder.bind(address, selectedAddress==position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress=holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }
    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Address) -> Unit)?=null

}