package hr.corvuspay.demoshop.helpers

import com.corvuspay.sdk.enums.Currency
import com.corvuspay.sdk.enums.Language

object Constants {

    // put your test or production store id here
    const val STORE_ID = 
    // put your test or production store secret here
    const val SECRET_KEY = ""

    // used to define working environment for SDK
    // possible values: "test" or "production"
    const val environmentSdk = "test"

    val CURRENCY = Currency.EUR
    const val BEST_BEFORE_SECONDS = 300L
    const val REQUIRE_COMPLETE = false
    // If parameter use_card_profiles is set to TRUE, SDK redirects transaction to webView regardless of whether CorvusPay application is installed or not!
    val USE_CARD_PROFILES = null
    // if user_card_profiles is set to TRUE, user_card_profiles_id must be a string != “”. Otherwise transaction can be redirected to Corvuspay app.
    const val USER_CARD_PROFILES_ID = "asdfghjqwerty76543"


// DO NOT MAKE CHANGES BELOW THIS LINE
    val LANGUAGE = Language.EN
    const val INTENT_EXTRA = "intent_extra"
    const val NO_INSTALMENTS = "no_instalments"
    const val FIXED_INSTALMENTS = "fixed_instalments"
    const val INSTALMENTS = "instalments"
    const val DYNAMIC_INSTALMENTS = "dynamic_instalments"
    const val INSTALMENTS_MAP = "instalments_map"
    const val CHECKOUT_DETAILS_TAG = "checkout_details"
    const val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

}