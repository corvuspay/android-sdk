package hr.corvuspay.demoshop.listeners

import hr.corvuspay.demoshop.models.Product

interface ShopProductClickListener {
    fun productClicked(product: Product)
}