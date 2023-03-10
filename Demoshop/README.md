&nbsp;
<p align="center">
<img src="https://www.corvuspay.com/wp-content/uploads/2019/10/CorvusPay-all-color@2x.png" alt="CorvusPay logo" /> 
</p>

   

# Usage

## Intro
**Demoshop** app provides a simple example for integration of CorvusPay SDK into your application.

## Usage

### Constants.kt
To start using Demoshop app you need to enter your configuration data.

configuration example (not to be used for testing):
 
```
// put your test or production store id here
const val STORE_ID = 12345
// put your test or production store secret here
const val SECRET_KEY = "mYdKwe30sDmsdalk22dLKDEeroKwdc"

// used to define working environment for SDK
// possible values: "test" or "production"
const val environmentSdk = "test"

val CURRENCY = Currency.EUR
const val BEST_BEFORE_SECONDS = 300L
const val REQUIRE_COMPLETE = false
// If parameter use_card_profiles is set to TRUE, SDK redirects transaction to webView regardless of whether CorvusPay application is installed or not!
val USE_CARD_PROFILES = null
// if user_card_profiles is set to TRUE, user_card_profiles_id must be a string != “”. Otherwise transaction can be redirected to Corvuspay app.
const val USER_CARD_PROFILES_ID = "asdfghjqwerty123"
```

**For more integration details please consult SDK documentation and CorvusPay integration manual!**
### CheckoutDetailsActivity.kt
Check this file for more details and examples on SDK usage.