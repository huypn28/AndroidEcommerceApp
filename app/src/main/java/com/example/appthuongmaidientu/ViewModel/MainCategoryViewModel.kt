package com.example.appthuongmaidientu.ViewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appthuongmaidientu.Data.Product
import com.example.appthuongmaidientu.Util.Resource

import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore:FirebaseFirestore
):ViewModel() {

    private val _specialProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    val specialProducts:StateFlow<Resource<List<Product>>> = _specialProducts


    private val _bestDeals=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    val bestDeals:StateFlow<Resource<List<Product>>> = _bestDeals


    private val _bestProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())

    val bestProducts:StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo=PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore
            .collection("Products")
            .whereEqualTo("category", "Special Products").get().addOnSuccessListener {result->
            //.whereGreaterThan("price", 3000000).get().addOnSuccessListener {result->
                val specialProductsList=result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))

                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))

                }
            }

    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDeals.emit(Resource.Loading())
        }

        firestore
            .collection("Products")
            //.whereEqualTo("category", "Best Deals").get().addOnSuccessListener {result->
            .whereNotEqualTo("offerPercentage", 0).get().addOnSuccessListener {result->

            val bestDealsList=result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Success(bestDealsList))

                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(it.message.toString()))

                }
            }

    }

    fun fetchBestProducts(){
      if (!pagingInfo.isPagingEnd) {
          viewModelScope.launch {
              _bestProducts.emit(Resource.Loading())
          }

          firestore
              .collection("Products").limit(pagingInfo.bestProductsPage * 10).get()
              .addOnSuccessListener { result ->
                  val bestProductsList = result.toObjects(Product::class.java)
                  pagingInfo.isPagingEnd = bestProductsList == pagingInfo.oldBestProducts
                  pagingInfo.oldBestProducts = bestProductsList
                  viewModelScope.launch {
                      _bestProducts.emit(Resource.Success(bestProductsList))

                  }
                  pagingInfo.bestProductsPage++
              }.addOnFailureListener {
                  viewModelScope.launch {
                      _bestProducts.emit(Resource.Error(it.message.toString()))

                  }
              }
      }
    }

}
internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd:Boolean = false
)