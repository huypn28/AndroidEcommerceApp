package com.example.appthuongmaidientu.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appthuongmaidientu.R
import com.example.appthuongmaidientu.Util.Resource
import com.example.appthuongmaidientu.ViewModel.CartViewModel
import com.example.appthuongmaidientu.databinding.ActivityShoppingBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import vn.zalopay.sdk.ZaloPaySDK

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController=findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Success ->{
                        val count=it.data?.size?:0
                        val bottomNavigation=findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number=count
                            backgroundColor=resources.getColor(R.color.g_blue)
                        }
                    }
                    else->Unit
                }
            }
        }

    }

}