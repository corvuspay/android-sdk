package hr.corvuspay.demoshop.helpers

import android.util.Log
import hr.corvuspay.demoshop.events.CartItemQuantityChanged
import hr.corvuspay.demoshop.events.CartItemRemoved
import hr.corvuspay.demoshop.models.Cart
import hr.corvuspay.demoshop.models.Product
import org.greenrobot.eventbus.EventBus

class CartSingleton {

    private object Holder {
        val INSTANCE = CartSingleton()
    }

    companion object {
        val instance: CartSingleton by lazy { Holder.INSTANCE }
    }

    private var cart: Cart = Cart()

    fun addProduct(product: Product) {
        cart.products.find { product.id == it.id }?.let {
            it.quantity++
        } ?: run {
            cart.products.add(product)
        }
        EventBus.getDefault().post(CartItemQuantityChanged(product.id, getQuantity()))
    }

    fun removeProduct(productId: Long, position: Int? = null) {
        cart.products.find { it.id == productId }?.let {
            if (cart.products.remove(it)) {
                EventBus.getDefault().post(CartItemRemoved(productId, position))
            } else {
                Log.e("CART ERROR", "Item not removed.")
            }
        } ?: run {
            Log.e("CART ERROR", "Product not found.")
        }
    }

    fun updateQuantity(productId: Long, quantity: Int) {
        cart.products.find { it.id == productId }?.let {
            it.quantity = quantity
        }
    }

    fun increaseQuantity(productId: Long) {
        cart.products.find { it.id == productId }?.let {
            it.quantity++
            EventBus.getDefault().post(CartItemQuantityChanged(productId, getQuantity()))
        } ?: run {
            Log.e("CART ERROR", "Product not found.")
        }
    }

    fun decreaseQuantity(productId: Long) {
        cart.products.find { it.id == productId }?.let {
            it.quantity--
            EventBus.getDefault().post(CartItemQuantityChanged(productId, getQuantity()))
        } ?: run {
            Log.e("CART ERROR", "Product not found.")
        }
    }

    fun emptyCart() {
        cart.products.clear()
    }

    fun getProducts() = cart.products

    fun getSubtotal() = cart.subtotal()

    fun getDiscountSubtotal() = cart.discountSubtotal()

    fun hasDiscounts() = cart.hasDiscounts()

    fun getQuantity() = cart.quantity()

    fun getContents(): String {
        var cartString = ""
        cart.products.forEach { cartString += "${it.name} x ${it.quantity}" }
        return cartString
    }
}