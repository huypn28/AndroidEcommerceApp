package com.example.appthuongmaidientu.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appthuongmaidientu.Data.Category
import com.example.appthuongmaidientu.Data.Product
import com.example.appthuongmaidientu.Util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category

):ViewModel(){

    private val _offerProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProduct = _offerProduct.asStateFlow()

    private val _bestProduct=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct = _bestProduct.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts(){
        viewModelScope.launch {
            _offerProduct.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage", 0).get()
            .addOnSuccessListener {
                val products=it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProduct.emit((Resource.Success(products)))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _offerProduct.emit((Resource.Error(it.message.toString())))
                }
            }
    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category)
            .whereEqualTo("offerPercentage", 0).get()
            .addOnSuccessListener {
                val products=it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProduct.emit((Resource.Success(products)))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _bestProduct.emit((Resource.Error(it.message.toString())))
                }
            }
    }




}