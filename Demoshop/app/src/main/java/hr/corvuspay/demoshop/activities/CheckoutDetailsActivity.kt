package hr.corvuspay.demoshop.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.corvuspay.sdk.CorvusPay
import com.corvuspay.sdk.constants.CheckoutCodes
import com.corvuspay.sdk.enums.CardType
import com.corvuspay.sdk.helpers.CorvusSDKException
import com.corvuspay.sdk.models.*
import hr.corvuspay.demoshop.R
import hr.corvuspay.demoshop.events.LoaderEvent
import hr.corvuspay.demoshop.helpers.*
import hr.corvuspay.demoshop.helpers.Constants.CHECKOUT_DETAILS_TAG
import hr.corvuspay.demoshop.helpers.Constants.DYNAMIC_INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.FIXED_INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.INSTALMENTS_MAP
import hr.corvuspay.demoshop.helpers.Constants.INTENT_EXTRA
import hr.corvuspay.demoshop.helpers.Constants.NO_INSTALMENTS
import hr.corvuspay.demoshop.helpers.Constants.REQUIRE_COMPLETE
import kotlinx.android.synthetic.main.activity_checkout_details.*
import kotlinx.android.synthetic.main.activity_checkout_details.layout_progress
import kotlinx.android.synthetic.main.layout_progress_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CheckoutDetailsActivity : AppCompatActivity() {

    private var installments: InstallmentParams? = CheckoutData.getInstallments()
    private var fixedInstallmentParams: InstallmentParams? = CheckoutData.getFixedInstallments()
    private var dynamicInstallments: InstallmentParams? = CheckoutData.getSampleDynamicParams()
    private var currentInstallmentsMap: InstallmentsMap? = CheckoutData.getSampleInstallmentsMap()
    private var checkoutType: String = ""
    lateinit var checkoutObject: Checkout

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_details)

        val bundle = intent.extras
        if (bundle != null) {
            checkoutType = bundle.getString(INTENT_EXTRA)!!
            setCheckoutType(checkoutType)
        }

        checkout()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun setCheckoutType(checkoutType: String) {
        when (checkoutType) {
            NO_INSTALMENTS -> {
                isInstallmentParamsVisible(false)
                isInstallmentMapsVisible(false)
                checkoutObject = createCheckoutObject().noInstallments().addCardProfiles().addSignature()
                setUICheckoutData(checkoutObject)
            }
            FIXED_INSTALMENTS -> {
                isInstallmentParamsVisible(true)
                isInstallmentMapsVisible(false)
                checkoutObject = createCheckoutObject().addFixedInstalments().addCardProfiles().addSignature()
                setUICheckoutData(checkoutObject)
            }
            INSTALMENTS -> {
                isInstallmentParamsVisible(true)
                isInstallmentMapsVisible(false)
                checkoutObject = createCheckoutObject().addInstalments().addCardProfiles().addSignature()
                setUICheckoutData(checkoutObject)
            }
            DYNAMIC_INSTALMENTS -> {
                isInstallmentParamsVisible(true)
                isInstallmentMapsVisible(false)
                checkoutObject = createCheckoutObject().addDynamicInstalments().addCardProfiles().addSignature()
                setUICheckoutData(checkoutObject)
            }
            INSTALMENTS_MAP -> {
                isInstallmentParamsVisible(false)
                isInstallmentMapsVisible(true)
                checkoutObject = createCheckoutObject().addInstalmentsMap().addCardProfiles().addSignature()
                setUICheckoutData(checkoutObject)
            }
        }
        Log.d(CHECKOUT_DETAILS_TAG, "checkout object ${checkoutObject}")
    }

    private fun checkout() {
        btn_installments_submit.setOnClickListener {
            try {
                CorvusPay.checkout(this, checkoutObject, Constants.environmentSdk)
            }catch (exception: CorvusSDKException){
                hr.corvuspay.demoshop.helpers.Helper.showError(
                    exception.message.toString(),
                    this,
                    this
                )
                Log.d("TAG","exception ${exception.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CheckoutCodes.REQUEST_CHECKOUT) {
            when (resultCode) {
                CheckoutCodes.RESULT_CODE_CHECKOUT_SUCCESS -> {
                    hr.corvuspay.demoshop.helpers.Helper.showSuccess(
                        this,
                        this)
                    Log.d("TAG","checkout object is $checkoutObject")
                }
                CheckoutCodes.RESULT_CODE_CHECKOUT_FAILURE ->{
                    hr.corvuspay.demoshop.helpers.Helper.showError(
                        "Order not processed.",
                        this,
                        this)
                }
                CheckoutCodes.RESULT_CODE_CHECKOUT_ABORTED -> {
                    hr.corvuspay.demoshop.helpers.Helper.showError(
                        "Checkout aborted.",
                        this,
                        this)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createCheckoutObject(): Checkout =
        Checkout(
            storeId = Constants.STORE_ID,
            orderId = CheckoutData.getRandomOrderId(),
            language = Constants.LANGUAGE,
            isSdk = true,
            version = "1.3",
            cart = CartSingleton.instance.getContents(),
            currency = Constants.CURRENCY,
            amount = CartSingleton.instance.getSubtotal(),
            requireComplete = REQUIRE_COMPLETE,
            discountAmount =
            if (CartSingleton.instance.hasDiscounts() && CartSingleton.instance.getDiscountSubtotal() != CartSingleton.instance.getSubtotal()) {
                tv_param_discountAmount.visibility = View.VISIBLE
                CartSingleton.instance.getDiscountSubtotal()
            } else {
                tv_param_discountAmount.visibility = View.GONE
                null
            }
        )

    private fun Checkout.noInstallments(): Checkout = this.copy(
        bestBefore = null,
        cardholder = CheckoutData.getCardholderDetails()
    )

    private fun Checkout.addFixedInstalments(): Checkout = this.copy(
        bestBefore = Helper.getFutureTimestamp(),
        cardholder = CheckoutData.getCardholderDetails()
    ).apply {
        return copy(installmentParams = fixedInstallmentParams)
    }

    private fun Checkout.addInstalments(): Checkout = this.copy(
        bestBefore = Helper.getFutureTimestamp(),
        cardholder = CheckoutData.getCardholderDetails()
    ).apply {
        return copy(installmentParams = installments)
    }

    private fun Checkout.addDynamicInstalments(): Checkout = this.copy(
        bestBefore = Helper.getFutureTimestamp(),
        cardholder = CheckoutData.getCardholderDetails()
    ).apply {
        return copy(installmentParams = dynamicInstallments)
    }

    private fun Checkout.addInstalmentsMap(): Checkout = this.copy(
        bestBefore = Helper.getFutureTimestamp(),
        cardholder = CheckoutData.getCardholderDetails()
    ).apply {
        return copy(installmentsMap = currentInstallmentsMap)
    }

    private fun Checkout.addSignature(): Checkout = this.apply {
        val stringToBeSigned = this.generateStringForSignature()
        val signedCheckout =
            EncryptionHelper.generateHashWithHmac256(stringToBeSigned, Constants.SECRET_KEY)
        return this.copy(signature = signedCheckout)
    }

    private fun Checkout.addCardProfiles(): Checkout = this.apply {
        val useCardProfiles = Constants.USE_CARD_PROFILES
        val userCardProfilesId = Constants.USER_CARD_PROFILES_ID

        return this.copy(useCardProfiles = useCardProfiles, userCardProfilesId = userCardProfilesId)
    }

    private fun isInstallmentParamsVisible(isVisible: Boolean) {
        if (isVisible) {
            tv_param_installmentParams.visibility = View.VISIBLE
        } else {
            tv_param_installmentParams.visibility = View.GONE
        }
    }

    private fun isInstallmentMapsVisible(isVisible: Boolean) {
        if (isVisible) {
            tv_param_installmentsMap.visibility = View.VISIBLE
        } else {
            tv_param_installmentsMap.visibility = View.GONE
        }
    }

    private fun isCardProfilesVisible(isVisible: Boolean){
        if (isVisible){
            tv_useCardProfiles.visibility = View.VISIBLE
            tv_userCardProfilesId.visibility = View.VISIBLE
        }else{
            tv_useCardProfiles.visibility = View.GONE
            tv_userCardProfilesId.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUICheckoutData(checkout: Checkout) {
        tv_param_storeId_value.text = checkout.storeId.toString()
        tv_param_orderId_value.text = checkout.orderId
        tv_param_currency_value.text = checkout.currency.toString()
        tv_param_amount_value.text = checkout.amount.toString()
        tv_param_discountAmount_value.text = checkout.discountAmount.toString()
        tv_param_requireComplete_value.text = checkout.requireComplete.toString()
        tv_param_cardholder_value.text =
            checkout.cardholder!!.cardholderName + " " + checkout.cardholder!!.cardholderSurname
        tv_param_bestBefore_value.text = checkout.bestBefore.toString()
        tv_param_installmentParams_value.text = showInstallments()
        tv_param_installmentsMap_value.text = showInstallments()
        tv_param_signature_value.text = checkout.signature
        tv_isSdk_value.text = checkout.isSdk.toString()
        tv_version_value.text = checkout.version
        if (Constants.USE_CARD_PROFILES != null){
            tv_useCardProfiles_value.text = Constants.USE_CARD_PROFILES.toString()
            tv_userCardProfilesId_value.text = Constants.USER_CARD_PROFILES_ID
            isCardProfilesVisible(true)
        }else{
            isCardProfilesVisible(false)
        }

    }

    private fun showInstallments(): String {
        return when (checkoutType) {
            FIXED_INSTALMENTS -> {
                return fixedInstallmentParams!!.numberOfInstallments.toString()
            }
            INSTALMENTS -> {
                return installments!!.paymentAll.toString()
            }
            DYNAMIC_INSTALMENTS -> {
                return dynamicInstallments!!.paymentAllDynamic.toString()
            }
            INSTALMENTS_MAP -> {
                val installmentsMapString: List<String> =
                    currentInstallmentsMap!!.cardConfigurations.map { it.toString() }

                return installmentsMapString.toString()
            }
            else -> {
                ""
            }
        }

    }

    fun showProgressBar() {
        layout_progress.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartLoaderEvent(loaderEvent: LoaderEvent){
        showProgressBar()
        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this, ShopActivity::class.java))
            CartSingleton.instance.emptyCart()
        }, 1000)


    }
}