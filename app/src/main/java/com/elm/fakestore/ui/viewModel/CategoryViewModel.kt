package com.elm.fakestore.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.fakestore.data.models.Category
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories
    
    private val _categoryProducts = MutableStateFlow<List<Products>>(emptyList())
    val categoryProducts: StateFlow<List<Products>> get() = _categoryProducts
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    
    private var currentOffset = 0
    private val pageSize = 10
    private var currentCategoryId: Int? = null

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiSerice.getCategories()
                if (response.isNotEmpty()) {
                    _categories.value = response
                } else {
                    _errorMessage.value = "No categories found"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun fetchCategoryProducts(categoryId: Int, offset: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiSerice.getCategoryProducts(
                    categoryId = categoryId,
                    offset = offset,
                    limit = limit
                )
                if (response.isNotEmpty()) {
                    if (offset == 0) {
                        _categoryProducts.value = response
                    } else {
                        _categoryProducts.value = _categoryProducts.value + response
                    }
                    currentOffset = offset + response.size
                    currentCategoryId = categoryId
                } else {
                    if (offset == 0) {
                        _errorMessage.value = "No products found in this category"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadMoreCategoryProducts() {
        if (!_isLoading.value && currentCategoryId != null) {
            fetchCategoryProducts(currentCategoryId!!, currentOffset, pageSize)
        }
    }
    

}
