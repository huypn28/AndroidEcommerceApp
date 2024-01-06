package com.example.appthuongmaidientu.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appthuongmaidientu.Data.Address
import com.example.appthuongmaidientu.EncryptionUtils
import com.example.appthuongmaidientu.Constants.KEYPASSWORD
import com.example.appthuongmaidientu.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):ViewModel(){

    private val _addNewAddress=MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress=_addNewAddress.asStateFlow()

    private val _error= MutableSharedFlow<String>()
    val error=_error.asSharedFlow()

    fun addAddress(address:Address){
        val validateInputs=validateInputs(address)
        if (validateInputs) {
            val encryptedAddress = encryptAddressData(address)

            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!)
                .collection("address").document()
                .set(encryptedAddress).addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }

                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }

                }
        }else{
            viewModelScope.launch { _error.emit("Nhập điền đầy đủ và chính xác thông tin") }
        }
    }
    private fun encryptAddressData(address: Address): Address {
        return Address(
            addressTiTle = EncryptionUtils.encrypt(address.addressTiTle.trim(), KEYPASSWORD),
            fullName = EncryptionUtils.encrypt(address.fullName.trim(), KEYPASSWORD),
            address = EncryptionUtils.encrypt(address.address.trim(), KEYPASSWORD),
            phone = EncryptionUtils.encrypt(address.phone.trim(), KEYPASSWORD),
            note = EncryptionUtils.encrypt(address.note.trim(), KEYPASSWORD)
        )
    }
    private fun validateInputs(address: Address): Boolean {
        val phone = address.phone.trim()

        return address.addressTiTle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.address.trim().isNotEmpty() &&
                phone.isNotEmpty() &&
                isValidPhoneNumber(phone) &&
                address.note.trim().isNotEmpty()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Kiểm tra xem có đúng 10 chữ số không và chữ số đầu tiên có phải là 0 không
        return phone.length == 10 && phone.startsWith("0")
    }
}