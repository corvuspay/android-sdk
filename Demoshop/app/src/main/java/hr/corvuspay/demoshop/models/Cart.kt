package hr.corvuspay.demoshop.models

import kotlin.collections.ArrayList

data class Cart(var products: ArrayList<Product> = ArrayList()) {
    fun subtotal() = products.sumByDouble {
        it.oldPrice?.let { discount -> discount * it.quantity } ?: (it.price * it.quantity)
    }

    fun discountSubtotal() = products.sumByDouble { it.price * it.quantity }

    fun hasDiscounts(): Boolean {
        products.forEach { if (it.oldPrice != null) return true }
        return false
    }

    fun quantity() = products.sumBy { it.quantity }
}