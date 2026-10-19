package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.ProductEntity
import com.sanaesencia.app.data.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableProducts: StateFlow<List<ProductEntity>> = repository.availableProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.seedInitialProducts()
        }
    }

    fun addProduct(name: String, description: String, price: Double, isAvailable: Boolean = true, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertProduct(
                ProductEntity(
                    name = name,
                    description = description,
                    price = price,
                    isAvailable = isAvailable
                )
            )
            onSuccess()
        }
    }

    fun toggleAvailability(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(isAvailable = !product.isAvailable))
        }
    }

    fun updateProduct(product: ProductEntity, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateProduct(product)
            onSuccess()
        }
    }

    fun deleteProduct(product: ProductEntity, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            onSuccess()
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
