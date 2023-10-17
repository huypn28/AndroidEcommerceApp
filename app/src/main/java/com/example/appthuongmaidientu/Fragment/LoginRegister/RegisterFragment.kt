package com.example.appthuongmaidientu.Fragment.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.appthuongmaidientu.Activities.ShoppingActivity
import com.example.appthuongmaidientu.Data.User
import com.example.appthuongmaidientu.R
import com.example.appthuongmaidientu.Util.RegisterValidation
import com.example.appthuongmaidientu.Util.Resource
import com.example.appthuongmaidientu.ViewModel.RegisterViewModel
import com.example.appthuongmaidientu.databinding.FragmentRegisterBinding
import com.google.android.material.tabs.TabLayout.TabGravity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

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

        binding.tvDoYouHaveAnAcconut.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

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

                            binding.buttonRegister.revertAnimation()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            Toast.makeText(requireContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show()


                        }
                        is Resource.Error ->{
                            Toast.makeText(requireContext(), "Đã có tài khoản", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, it.message.toString())
                            binding.buttonRegister.revertAnimation()

                        }
                        else -> Unit

                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                viewModel.validation.collect{validation ->
                    if (validation.email is RegisterValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.edEmailRegister.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                        }
                    }

                    if (validation.password is RegisterValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.edPasswordRegister.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }

                }
            }
        }
    }

}