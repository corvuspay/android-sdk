package hr.corvuspay.demoshop.helpers

import com.corvuspay.sdk.enums.CardType
import com.corvuspay.sdk.models.Cardholder
import com.corvuspay.sdk.models.DynamicInstallmentParams
import com.corvuspay.sdk.models.InstallmentParams
import com.corvuspay.sdk.models.InstallmentsMap
import kotlin.random.Random

object CheckoutData {

    fun getCardholderDetails(): Cardholder =
        Cardholder(
            cardholderZipCode = "10000",
            cardholderName = "Hrvoje",
            cardholderSurname = "Horvat",
            cardholderPhone = "00385912345678",
            cardholderEmail = "neki.mail@domena.com",
            cardholderCountryCode = "HR",
            cardholderCountry = "Croatia",
            cardholderCity = "Zagreb",
            cardholderAddress = "Ulica bb"
        )

    fun getRandomOrderId(): String = Random.nextLong(100000, 10000000000).toString()

    // most common setup for merchant
    fun getInstallments(): InstallmentParams{
        return InstallmentParams.createUsingPaymentAllWith(true,2,99)
    }

    // this setup allows merchant to set different instalment options for different card types
    fun getSampleDynamicParams(): InstallmentParams {
        val dynamicInstallmentData = DynamicInstallmentParams.Builder()
            .addVisa(true, 0, 0)
            .addMaster(true, 2, 12)
            .addMaestro(true, 2, 12)
            .addJcb(false, 0, 0)
            .addDiscover(false, 0, 0)
            .addDiners(true, 2, 6)
            .addDina(false, 0, 0)
            .build()
        return InstallmentParams.createUsingPaymentAllDynamicWith(dynamicInstallmentData)
    }

    // most flexible method with custom instalment and amount options for each card type
    fun getSampleInstallmentsMap(): InstallmentsMap {
        val visaDiscounts = InstallmentsMap.Discount.Builder()
            .add(1, 539.99, 536.99)
            .add(2, 539.99, 537.99)
            .add(3, 539.99, 538.99)
            .add(4, 539.99, 539.99)
            .add(5, 539.99, 539.99)
            .add(6, 540.99, 540.99)
            .build()
        val dinersDiscounts = InstallmentsMap.Discount.Builder()
            .add(1, 539.99, 536.99)
            .add(2, 539.99, 537.99)
            .add(3, 539.99, 538.99)
            .add(4, 539.99, 539.99)
            .add(5, 539.99, 539.99)
            .add(6, 540.99, 540.99)
            .build()
        return InstallmentsMap.Builder()
            .addDiscountsFor(CardType.Visa, visaDiscounts)
            .addDiscountsFor(CardType.Diners, dinersDiscounts)
            .build()
    }

    // fixed instalment number - can be used if merchant wants to offer only one instalment option
    fun getFixedInstallments(): InstallmentParams{
        return InstallmentParams.createUsingNumberOfInstallmentsWith(5)
    }

}