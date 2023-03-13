package hr.corvuspay.demoshop.helpers

import java.io.Serializable

data class CheckoutExtras(
    val storeId: String,
    val orderNumber: String,
    val signature: String,
    val language: String,
    val cart: String,
    val currency: String,
    val amount: String,
    val requireComplete: Boolean,
    val cardholderName: String,
    val cardholderSurname: String,
    val merchantName: String
) : Serializable