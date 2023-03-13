package hr.corvuspay.demoshop.models

import java.io.Serializable

data class Product(
    val id: Long,
    var name: String,
    var price: Double,
    var description: String,
    var quantity: Int,
    var imageResource: Int,
    var oldPrice: Double? = null
) : Serializable