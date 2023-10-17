package com.example.appthuongmaidientu.ViewModel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appthuongmaidientu.Data.Category
import com.example.appthuongmaidientu.ViewModel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFacetory (
    private val firebase:FirebaseFirestore,
    private val category:Category

        ):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firebase,category) as T
    }


}