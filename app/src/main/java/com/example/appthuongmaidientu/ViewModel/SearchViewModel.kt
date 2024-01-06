package com.example.appthuongmaidientu.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appthuongmaidientu.Data.Product
import com.example.appthuongmaidientu.Constants.PRODUCTS_COLLECTION
import com.example.appthuongmaidientu.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){
    val search = MutableLiveData<Resource<List<Product>>>()
    private val productsCollection = Firebase.firestore.collection(PRODUCTS_COLLECTION)


    fun searchProducts(searchQuery: String) {
        search.postValue(Resource.Loading())
        SearchProducts(searchQuery).addOnCompleteListener {
            if (it.isSuccessful) {
                val productsList = it.result!!.toObjects(Product::class.java)
                search.postValue(Resource.Success(productsList))

            } else
                search.postValue(Resource.Error(it.exception.toString()))

        }
    }

    fun SearchProducts(searchQuery: String) = productsCollection
        .orderBy("name")
        .startAt(searchQuery)
        .endAt("$searchQuery+\uf8ff")
        .limit(100)
        .get()




}