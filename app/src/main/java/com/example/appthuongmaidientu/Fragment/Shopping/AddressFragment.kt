package com.example.appthuongmaidientu.Fragment.Shopping

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
import com.example.appthuongmaidientu.Data.Address
import com.example.appthuongmaidientu.EncryptionUtils
import com.example.appthuongmaidientu.Constants.KEYPASSWORD
import com.example.appthuongmaidientu.Util.Resource
import com.example.appthuongmaidientu.ViewModel.AddressViewModel
import com.example.appthuongmaidientu.databinding.FragmentAddressBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint

class AddressFragment:Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarAddress.visibility=View.INVISIBLE
                        findNavController().navigateUp()

                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddressBinding.inflate(inflater)
        return binding.root

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address=args.address
        if (address==null){
            binding.buttonDelelte.visibility=View.GONE
        }else{
            binding.apply {
                // Giải mã dữ liệu
                val decryptedTitle = EncryptionUtils.decrypt(address.addressTiTle, KEYPASSWORD)
                val decryptedFullName = EncryptionUtils.decrypt(address.fullName, KEYPASSWORD)
                val decryptedAddress = EncryptionUtils.decrypt(address.address, KEYPASSWORD)
                val decryptedPhone = EncryptionUtils.decrypt(address.phone, KEYPASSWORD)
                val decryptedNote = EncryptionUtils.decrypt(address.note, KEYPASSWORD)

                // Hiển thị dữ liệu đã giải mã trên giao diện người dùng
                edAddressTitle.setText(decryptedTitle)
                edFullName.setText(decryptedFullName)
                edAddress.setText(decryptedAddress)
                edPhone.setText(decryptedPhone)
                edNote.setText(decryptedNote)

            }
        }

        binding.apply {
            buttonSave.setOnClickListener {


                val addressTitle=edAddressTitle.text.toString()
                val fullName=edFullName.text.toString()
                val address=edAddress.text.toString()
                val phone=edPhone.text.toString()
                val note=edNote.text.toString()



                val addresses=Address(addressTitle,fullName,address,phone,note)

                viewModel.addAddress(addresses)
            }
        }
    }
}