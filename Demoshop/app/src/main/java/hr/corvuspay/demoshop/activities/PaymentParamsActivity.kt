package hr.corvuspay.demoshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.corvuspay.sdk.enums.CardType
import com.corvuspay.sdk.enums.Currency
import com.corvuspay.sdk.enums.Language
import com.corvuspay.sdk.models.Cardholder
import com.corvuspay.sdk.models.Checkout
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.helpers.CartSingleton
import hr.corvuspay.demoshop.helpers.Constants
import hr.corvuspay.demoshop.helpers.Constants.DYNAMIC_INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.FIXED_INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.INSTALMENTS_MAP
import hr.corvuspay.demoshop.helpers.Constants.INTENT_EXTRA
import hr.corvuspay.demoshop.helpers.Constants.NO_INSTALMENTS
import hr.corvuspay.demoshop.helpers.EncryptionHelper
import kotlinx.android.synthetic.main.activity_payment_params.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PaymentParamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_params)

        setComponents()
    }

    private fun setComponents() {
        params_menu_simple.setOnClickListener {
            setCheckoutType(NO_INSTALMENTS)
        }

        params_menu_fixed.setOnClickListener {
            setCheckoutType(FIXED_INSTALMENTS)
        }

        params_menu_payment.setOnClickListener {
            setCheckoutType(INSTALMENTS)
        }

        params_menu_dynamic.setOnClickListener {
            setCheckoutType(DYNAMIC_INSTALMENTS)
        }

        params_menu_installment_map.setOnClickListener {
            setCheckoutType(INSTALMENTS_MAP)
        }
    }

    private fun setCheckoutType(checkoutType: String){
        val intent = Intent(this, CheckoutDetailsActivity::class.java)
        intent.putExtra(INTENT_EXTRA,checkoutType)
        startActivity(intent)
    }

}