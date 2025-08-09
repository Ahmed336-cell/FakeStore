package com.elm.fakestore.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.serialization.serializers.MutableStateFlowSerializer
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Response

class HomeViewModel : ViewModel() {
    private val _product = MutableStateFlow<List<Products>>(emptyList())
    val product : StateFlow<List<Products>>  get() =  _product
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    
    private var currentOffset = 0
    private val pageSize = 10

  fun  fetchProducts(offset: Int = 0, limit: Int = 10) {
      viewModelScope.launch {
          _isLoading.value = true
          _errorMessage.value = null
          try {
              val response = RetrofitClient.apiSerice.getProducts(offset = offset, limit = limit)
                if (response.isNotEmpty()) {
                    if (offset == 0) {
                        _product.value = response
                    } else {
                        _product.value = _product.value + response
                    }
                    currentOffset = offset + response.size
                } else {
                    if (offset == 0) {
                        _errorMessage.value = "No products found"
                    }
                }

          }catch (e: Exception) {
              _errorMessage.value = e.message
          } finally {
              _isLoading.value = false
          }
      }
    }
    
    fun loadMoreProducts() {
        if (!_isLoading.value) {
            fetchProducts(offset = currentOffset, limit = pageSize)
        }
    }
    
    fun refreshProducts() {
        currentOffset = 0
        fetchProducts(offset = 0, limit = pageSize)
    }


}