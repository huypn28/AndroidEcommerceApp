package com.example.appthuongmaidientu.Fragment.Shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appthuongmaidientu.Adapter.AddressAdapter
import com.example.appthuongmaidientu.Adapter.BillingProductsAdapter
import com.example.appthuongmaidientu.Data.Address
import com.example.appthuongmaidientu.Data.CartProduct
import com.example.appthuongmaidientu.Data.Order.Order
import com.example.appthuongmaidientu.Data.Order.OrderStatus
import com.example.appthuongmaidientu.R
import com.example.appthuongmaidientu.Util.HorizontalItemDecoration
import com.example.appthuongmaidientu.Util.Resource
import com.example.appthuongmaidientu.ViewModel.BillingViewModel
import com.example.appthuongmaidientu.ViewModel.OrderViewModel
import com.example.appthuongmaidientu.databinding.FragmentBillingBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment :Fragment(){
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = .0f

    private var selectedAddress :Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products=args.products.toList()
        totalPrice=args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBillingBinding.inflate(inflater)
        return binding.root

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBillingProductsRv()
        setupAddressesRv()

        if (!args.payment){
            binding.apply {
                buttonPlaceOrder.visibility=View.INVISIBLE
                totalBoxContainer.visibility=View.INVISIBLE
                middleLine.visibility=View.INVISIBLE
                bottomLine.visibility=View.INVISIBLE
            }
        }

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment2_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success ->{
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility=View.GONE
                    }
                    is Resource.Error ->{
                        binding.progressbarAddress.visibility=View.GONE
                        Toast.makeText(requireContext(), "Lỗi ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else-> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Đơn hàng của bạn đã được đặt", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), "Lỗi ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else-> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)
        binding.tvTotalPrice.text="${String.format("%.0f", totalPrice)} VNĐ"

        addressAdapter.onClick={
            selectedAddress=it
            if (!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment2_to_addressFragment, b)
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress==null){
                Toast.makeText(requireContext(), "Hãy chọn địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationdialog()
        }
    }

    private fun showOrderConfirmationdialog() {
        val alertDialog= AlertDialog.Builder(requireContext())
            .apply {
                setTitle("Đặt hàng")
                setMessage("Bạn muốn đặt hàng?")
                setNegativeButton("Hủy"){dialog,_ ->
                    dialog.dismiss()
                }
                setPositiveButton("Đồng ý"){dialog,_ ->
                    val order= Order(
                        OrderStatus.Ordered.status,
                        totalPrice,
                        products,
                        selectedAddress!!
                    )
                    orderViewModel.placeOrder(order)
                    dialog.dismiss()
                }
            }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupAddressesRv() {
        binding.rvAddress.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter=addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter=billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())

        }
    }
}