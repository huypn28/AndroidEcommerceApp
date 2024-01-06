package com.example.appthuongmaidientu.Fragment.Shopping

import android.app.AlertDialog
import android.content.Intent
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
import org.json.JSONException
import org.json.JSONObject
import vn.momo.momo_partner.AppMoMoLib
import kotlin.random.Random


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

    private var amount = "10000"
    private val fee = "0"
    var environment = 0 //developer default

    private val merchantName = "HoangNgoc"
    private val merchantCode = "MOMOC2IC20220510"
    private val merchantNameLabel = "Nhà cung cấp"
    private val description = "Thanh toán online"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products=args.products.toList()
        totalPrice=args.totalPrice
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

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
                buttonPlaceOrderKhiGiaoHang.visibility=View.INVISIBLE
                buttonPlaceOrderQuaMomo.visibility=View.INVISIBLE
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
//khi giao
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonPlaceOrderKhiGiaoHang.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonPlaceOrderKhiGiaoHang.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Đơn hàng của bạn đã được đặt", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.buttonPlaceOrderKhiGiaoHang.revertAnimation()
                        Toast.makeText(requireContext(), "Lỗi ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else-> Unit
                }
            }
        }
//qua momo
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonPlaceOrderQuaMomo.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonPlaceOrderQuaMomo.revertAnimation()

                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Đơn hàng của bạn đã được đặt", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        binding.buttonPlaceOrderQuaMomo.revertAnimation()
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

        binding.buttonPlaceOrderKhiGiaoHang.setOnClickListener {
            if (selectedAddress==null){
                Toast.makeText(requireContext(), "Hãy chọn địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationdialog()
        }
        binding.buttonPlaceOrderQuaMomo.setOnClickListener {
            if (selectedAddress==null){
                Toast.makeText(requireContext(), "Hãy chọn địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationdialogMOMO()

        }
    }

    private fun showOrderConfirmationdialogMOMO() {
        val alertDialog= AlertDialog.Builder(requireContext())
            .apply {
                setTitle("Đặt hàng")
                setMessage("Bạn muốn đặt hàng?")
                setNegativeButton("Hủy"){dialog,_ ->
                    dialog.dismiss()
                }
                setPositiveButton("Đồng ý"){dialog,_ ->


                    requestPayment()
                    val order= Order(
                        OrderStatus.Paid.status,
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
    fun generateRandomNumber(): String {
        val random = Random(System.currentTimeMillis())
        val randomNumber = StringBuilder()

        // Tạo 12 chữ số ngẫu nhiên
        repeat(12) {
            randomNumber.append(random.nextInt(10))
        }

        return randomNumber.toString()
    }
    //Get token through MoMo app
    private fun requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)
        val orderId = generateRandomNumber()

        val eventValue: MutableMap<String, Any> = HashMap()
        //client Required
        eventValue["merchantname"] =
            merchantName //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue["merchantcode"] =
            merchantCode //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue["amount"] = "${String.format("%.0f", totalPrice)}" //Kiểu integer
        eventValue["orderId"] =
            "$orderId" //uniqueue id cho BillId, giá trị duy nhất cho mỗi BILL
        eventValue["orderLabel"] = "Mã đơn hàng" //gán nhãn

        //client Optional - bill info
        eventValue["merchantnamelabel"] = "Dịch vụ" //gán nhãn
        eventValue["fee"] = "0" //Kiểu integer
        eventValue["description"] = description //mô tả đơn hàng - short description

        //client extra data
        eventValue["requestId"] = merchantCode + "merchant_billId_" + System.currentTimeMillis()
        eventValue["partnerCode"] = merchantCode
        //Example extra data
        val objExtraData = JSONObject()
        try {
            objExtraData.put("site_code", "008")
            objExtraData.put("site_name", "CGV Cresent Mall")
            objExtraData.put("screen_code", 0)
            objExtraData.put("screen_name", "Special")
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3")
            objExtraData.put("movie_format", "2D")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        eventValue["extraData"] = objExtraData.toString()
        eventValue["extra"] = ""
        AppMoMoLib.getInstance().requestMoMoCallBack(requireActivity(), eventValue)
    }

    //Get token callback from MoMo app an submit to server side
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE

                    val token = data.getStringExtra("data") //Token response
                    val phoneNumber = data.getStringExtra("phonenumber")
                    var env = data.getStringExtra("env")
                    if (env == null) {
                        env = "app"
                    }
                    if (token != null && token != "") {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order

                    } else {
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    val message =
                        if (data.getStringExtra("message") != null) data.getStringExtra("message") else "Thất bại"
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                } else {
                    //TOKEN FAIL
                }
            } else {
            }
        } else {
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