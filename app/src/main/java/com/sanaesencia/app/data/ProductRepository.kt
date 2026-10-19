package com.sanaesencia.app.data

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    private val seedProducts = listOf(
        ProductEntity(name = "Kit de Bienestar Integrativo", description = "Set de aromaterapia con roll-on, bruma y aceite esencial.", price = 24990.0, imageUrl = "kit_bienestar", stock = 15),
        ProductEntity(name = "Roll-on Calma y Serenidad", description = "Aceites esenciales de lavanda y manzanilla para momentos de estrés.", price = 7990.0, imageUrl = "rollon_calma", stock = 20),
        ProductEntity(name = "Bruma de Almohada Sueño Dulce", description = "Bruma botánica relajante para el descanso nocturno.", price = 9990.0, imageUrl = "bruma_sueno", stock = 12),
        ProductEntity(name = "Roll-on Concentración y Foco", description = "Aroma de menta y romero para potenciar el estudio.", price = 7990.0, imageUrl = "rollon_foco", stock = 18)
    )

    suspend fun seedInitialProducts() {
        try {
            val count = productDao.getProductCount()
            if (count == 0) {
                productDao.insertProducts(seedProducts)
            }
        } catch (e: Throwable) {
            // Ignore
        }
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }
}
