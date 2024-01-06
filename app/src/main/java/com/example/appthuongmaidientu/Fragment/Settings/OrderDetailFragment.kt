package com.example.appthuongmaidientu.Fragment.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appthuongmaidientu.Adapter.BillingProductsAdapter
import com.example.appthuongmaidientu.Data.Order.OrderStatus
import com.example.appthuongmaidientu.Data.Order.getOrderStatus
import com.example.appthuongmaidientu.EncryptionUtils
import com.example.appthuongmaidientu.Constants.KEYPASSWORD
import com.example.appthuongmaidientu.Util.VerticalItemDecoration
import com.example.appthuongmaidientu.databinding.FragmentOrderDetailBinding

class OrderDetailFragment:Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order=args.order
        setupOrderRv()

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            tvOrderId.text="Đơn hàng #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Paid.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderStatus=when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Paid -> 1
                is OrderStatus.Confirmed -> 2
                is OrderStatus.Shipped -> 3
                is OrderStatus.Delivered -> 4
                else -> 0
            }

            stepView.go(currentOrderStatus, false)

            if (currentOrderStatus==3){
                stepView.done(true)
            }



// Giải mã dữ liệu
            val decryptedFullName = EncryptionUtils.decrypt(order.address.fullName, KEYPASSWORD)
            val decryptedAddress = EncryptionUtils.decrypt(order.address.address, KEYPASSWORD)
            val decryptedPhoneNumber = EncryptionUtils.decrypt(order.address.phone, KEYPASSWORD)

// Hiển thị dữ liệu đã giải mã lên giao diện người dùng
            tvFullName.text = decryptedFullName
            tvAddress.text = decryptedAddress
            tvPhoneNumber.text = decryptedPhoneNumber

            tvTotalPrice.text= "${String.format("%.0f", order.totalPrice)} VNĐ"

        }
        billingProductsAdapter.differ.submitList(order.products)

    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter=billingProductsAdapter
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }


}