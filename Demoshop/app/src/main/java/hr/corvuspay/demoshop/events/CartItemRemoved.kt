package hr.corvuspay.demoshop.events

data class CartItemRemoved(val productId: Long, val position: Int? = null)