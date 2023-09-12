package com.example.appthuongmaidientu.Fragment.LoginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appthuongmaidientu.Data.User
import com.example.appthuongmaidientu.R
import com.example.appthuongmaidientu.Util.Resource
import com.example.appthuongmaidientu.ViewModel.RegisterViewModel
import com.example.appthuongmaidientu.databinding.FragmentRegisterBinding
import com.google.android.material.tabs.TabLayout.TabGravity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment:Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentRegisterBinding.inflate(inflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonRegister.setOnClickListener {
                val  user=User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()

                )
                val  password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user,password)
            }
            //thu thap khi khoi tao
            lifecycleScope.launchWhenStarted {
                viewModel.register.collect{
                    when(it){
                        is Resource.Loading ->{
                            binding.buttonRegister.startAnimation()
                        }
                        is Resource.Success ->{
                            Log.d("test", it.data.toString())
                            binding.buttonRegister.revertAnimation()

                        }
                        is Resource.Error ->{
                            Log.e(TAG, it.data.toString())
                            binding.buttonRegister.revertAnimation()

                        }
                        else -> Unit

                    }
                }
            }
            lifecycleScope.launchWhenStarted {  }
        }
    }

}