&nbsp;
<p align="center">
<img src="https://www.corvuspay.com/wp-content/uploads/2019/10/CorvusPay-all-color@2x.png" alt="CorvusPay logo" /> 
</p>

# **Content**
- [Installation](#installation)
- [Usage](#usage)
	- [Intro](#intro)
	- [Checkout](#checkout)
		- [Initialization](#initialization)
 		- [Result](#result)
		- [Checkout object](#checkout-object)
	- [Installments](#installments)
		- [Installments Map](#installments-map)
			- [CardConfiguration object](#cardconfiguration-object)
			- [Discount object](#discount-object)
		- [Installment Parameters](#installment-parameters)
			- [Payment All Dynamic](#payment-all-dynamic)
			- [Payment All](#payment-all)
			- [Number Of Installments](#number-of-installments)
 - [FAQ](#faq)
   
# Installation
Start using the **Corvus Pay SDK** in your project by adding it to your project dependencies, and adding the following dependency in your application's build.gradle file:
`implementation files('{path to sdk .aar file}')`

&nbsp;
# Usage

## Intro
**CorvusPay SDK** provides a simple integration of CorvusPay services into your application, allowing fast and secure transactions through our payment gateway.

The SDK will use the CorvusPay mobile app by default if the user has it installed. Otherwise, the web interface will be opened from within your app and handle the process from there.

With the SDK, the checkout parameters are specified, which will be visible to the customer while the CorvusPay application is handling their transaction on the checkout screen. Also, by using the SDK, custom installment payment options can be defined, and discounts for transactions can be set up. 

To use the SDK, the checkout process needs to be initialized and all required parameters need to be provided.

&nbsp;
## Checkout

### Initialization
To initiate a checkout process, the SDK's `checkout()` method should be called, which requires you to provide the `Activity` from which the method is called,`Checkout` object which holds information about the transaction and a 'environmentSdk' flag to choose on which environment(test or production) the SDK will work.
```kotlin
CorvusPay.checkout(activity: Activity, checkout: Checkout, environmentSdk: String)
```
When the Checkout object is created, it needs to be signed, before being used as an argument in the above-mentioned function. 

Signing is done by retrieving the string interpretation of the Checkout object using its `generateStringForSignature(): String` function. The result of the function is used as the content that will be hashed and added into the Checkout object by using its `copy(signature = ... )` function. 

In order to create the hash, the SHA256 algorithm is used, along with the secret key known to the merchant and Corvus.

&nbsp;
### Result
The `checkout()` function will start a new activity using the `startActivityForResult()` function, with the following request code: `com.corvuspay.sdk.constants.CheckoutCodes.REQUEST_CHECKOUT`.  If the end-user has the CorvusPay application installed, it will be used to complete the checkout process. If not, the process will continue within a WebView.

Once the checkout process is finished, a response will be forwarded to the application which started it. In order to catch the result, the `onActivityResult()` function needs to be overridden. The possible result codes, contained in the `com.corvuspay.sdk.constants.CheckoutCodes` file, can be: 
- `RESULT_CODE_CHECKOUT_SUCCESS`
- `RESULT_CODE_CHECKOUT_FAILURE`
	- Returned if the checkout process was canceled because of an error
- `RESULT_CODE_CHECKOUT_ABORTED`
	- Returned if the end-user canceled the checkout process manually

#### Example
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	if (requestCode == CheckoutCodes.REQUEST_CHECKOUT) {
		when (resultCode) {
			CheckoutCodes.RESULT_CODE_CHECKOUT_SUCCESS -> ...
			CheckoutCodes.RESULT_CODE_CHECKOUT_FAILURE -> ...
			CheckoutCodes.RESULT_CODE_CHECKOUT_ABORTED -> ...
		}
	}
	super.onActivityResult(requestCode, resultCode, data)
}
```

&nbsp;
### Checkout object
The Checkout object (`com.corvuspay.sdk.models.Checkout`) is used to define transaction information. Here is the breakdown of the required and optional parameters:

#### Required:
- **storeId** (`Int`)
	- Store ID provided by Corvus
- **orderId** (`String`)
	- Unique order identifier
- **language** (`com.corvuspay.sdk.enums.Language`)
	- Enum value defining the language preference for the checkout process
- **cart** (`String`)
	- String representing objects which will be purchased
- **currency** (`com.corvuspay.sdk.enums.Currency`)
	- Enum value defining the currency which will be used during checkout
- **amount** (`Double`)
	- Total cost of the purchase
- **signature** (`String`)
	- Requests must be signed/verified using HMAC-SHA256 where the key is a value known to the CorvusPay and the merchant. More details available in official integration manual.
- **requireComplete** (`Boolean`)

#### Optional:
- **discountAmount** (`Double`)
	- Total amount remaining after a discount is applied
- **preselectedCard** (`com.corvuspay.sdk.enums.CardType`)
	- Enum value defining the card type which the user will be using for checkout
- **cardholder** (`com.corvuspay.sdk.models.Cardholder`)
	- Object containing optional cardholder data
- **bestBefore** (`Long`)
	- Value defining when the purchase time for the transaction expires (Epoch timestamp)
	- Maximum time a merchant may specify is 900 seconds (15 minutes) into the future
	- If not set, or if the value is too far into the future, it is set to 900 seconds (15 minutes)
	- If a past timestamp is provided, a `CorvusSDKException` will be thrown with the following message:
		`BestBefore parameter has to be a valid future timestamp.`
- **installmentParams** (`com.corvuspay.sdk.models.InstallmentParams`)
	- InstallmentParams object
- **installmentsMap** (`com.corvuspay.sdk.models.InstallmentsMap`)
	- InstallmentsMap object

&nbsp;
## Installments
If custom payment/installment options want to be used, custom behavior needs to be defined for the checkout process. There are 4 ways to define custom installment behaviours, defined by priority: 

1. **installmentsMap** 
2. **paymentAllDynamic**
3. **paymentAll**
4. **numberOfInstallments**

`NumberOfInstallments`, `paymentAll`, and `paymentAllDynamic` are grouped together in the `InstallmentParams` class. In order to define them, the corresponding methods inside the `InstallmentParams` class need to be used.

When adding the installment options to the Checkout object, **only one** option can be used, either `InstallmentsMap` or `InstallmentParams`.
If more than one option is defined, a `CorvusSDKException` will be thrown with the following message: 
`"Either installmentParams or installmentsMap object may be defined."`

**Note 1:** Use of the InstallmentsMap is recommended when there is need for a complex customization of installment configurations.
 **Note 2:** The first step in the CorvusPay checkout process is fetching the store configuration (`storeConfig`) from the server and cross-referencing the installment configurations. The user will be able to choose only those installment options which were defined both in the `Checkout` object, and in `storeConfig`.

&nbsp;
### Installments Map
The InstallmentsMap object is the most customizable option for defining installments, which will be used during the checkout process.

Each `InstallmentsMap` object has the following variable:
- **cardConfigurations** (`List<CardConfigurations>`)

To set up a card discount configurations for any card type, use the `InstallmentsMap.Builder()` object. The Builder has two functions used for adding discount data for a certain card.

When a list of defined Discount objects is available, the following function is used:
`fun addDiscountsFor(cardType: CardType, discounts: List<Discount>) : InstallmentsMap.Builder`

If single discount data wants to be added for a card, the following function is used:
`fun addDiscountFor(cardType: CardType, discount: Discount) : InstallmentsMap.Builder`

Finally, the `build()` function returns an object of type `InstallmentsMap`, which contains the installment configurations for each card type.

**Note 1**: If amounts are added multiple times for a certain card type and installment number, only the latest entry will be taken into account.
**Note 2:** In order to avoid the application crashing if no card discount data is added to the Builder before calling `build()`, surround the calling code with a try-catch block.

&nbsp;
#### CardConfiguration object
A CardConfiguration object cannot be instantiated, it is only available within already instantiated InstallmentsMap objects.

Each `CardConfiguration` object has the following variables:
- **cardType** (`CardType`)
- **discounts** (`List<Discount>`)

&nbsp;
#### Discount object
The Discount object (`com.corvuspay.sdk.models.InstallmentsMap.Discount`) is used to define discount data, which can be applied to any CardType using the InstallmentsMap Builder.

Each `Discount` object has the following variables and conditions:
- **numberOfInstallments** (`Int`)
	- *numberOfInstallments >= 1 && numberOfInstallments <= 99*
- **amount** (`Double`)
	- *amount > 0*
- **discountedAmount** (`Double`)
	- *discountedAmount <= amount*

In order to instantiate a **single Discount object**, the class' constructor can be used.
- Please note, during the construction of the object, an `IllegalArgumentException` can be thrown if any of the aforementioned conditions are not met.

In order to instantiate a **list of Discount objects**, the provided `Discount.Builder()` can be used. When the Builder object is created, individual Discounts can be added by calling the following function:
`add(numberOfInstallments: Int, amount: Double, discountedAmount: Double) : InstallmentsMap.Discount.Builder`.
- Please note, when calling this function, an `IllegalArgumentException` can be thrown if any of the aforementioned conditions are not met.

Finally, in order to construct the list of discounts, the `build()` function needs to be called.
- Please note, if not a single `add()` function was called before the `build()` function is called, a `CorvusSDKException` will be thrown with the following message: `"At least one discount definition must be present."`

**Note 1:** If for a certain number of installments, the amounts are added multiple times, only the latest entry will be taken into account.
**Note 2:** In order to avoid the application crashing in case of a malformed constructor call, malformed `add()` function call, or if no discount definitions are added to the Builder before calling `build()`, surround the calling code with a try-catch block.

&nbsp;
#### Usage example

**E.g.** `Friday discounts for Visa and Diners` 
```kotlin
val fridayDiscounts = InstallmentsMap.Discount.Builder()  
	.add(numberOfInstallments = 1, amount = 1000, discountedAmount = 950)  
	.add(numberOfInstallments = 2, amount = 1000, discountedAmount = 950)
	.build()

val customInstallmentsMap = InstallmentsMap.Builder()
	.addDiscountsFor(CardType.Visa, fridayDiscounts)
	.addDiscountsFor(CardType.Diners, fridayDiscounts)
	.build()
```

&nbsp;
### Installment Parameters
The InstallmentParams object offers the remaining three options for defining installments.

Each `InstallmentParams` object has the following variables:
- **paymentAllDynamic** (`DynamicInstallmentParams?`)
- **paymentAll** (`Triple<Boolean, Int, Int>?`)
- **numberOfInstallments** (`Int?`)

All three variables are `null` by default, and they can be changed only by using the three static functions defined inside the class. This way, only one option for defining installments can be defined for a single `InstallmentParams` object.

&nbsp;
#### Payment All Dynamic
This option enables specifying a dynamic number of installments for each card type, which will be used during the checkout process.

In order to use the option, a `DynamicInstallmentParams` object needs to be created, by using its Builder. 
This object will then be used to create the `InstallmentParams` object, using the following function: 
`InstallmentParams.createUsingPaymentAllDynamicWith(dynamicInstallmentParams: DynamicInstallmentParams): InstallmentParams`

Each `DynamicInstallmentParams` object has the following variables:
- **paymentAllDynamic** (`Boolean`)
- **paymentAmex** (`Triple<Boolean, Int, Int>`)
- **paymentDina** (`Triple<Boolean, Int, Int>`)
- **paymentDiners** (`Triple<Boolean, Int, Int>`)
- **paymentDiscover** (`Triple<Boolean, Int, Int>`)
- **paymentJcb** (`Triple<Boolean, Int, Int>`)
- **paymentMaster** (`Triple<Boolean, Int, Int>`)
- **paymentMaestro** (`Triple<Boolean, Int, Int>`)
- **paymentVisa** (`Triple<Boolean, Int, Int>`)

All `payment<cardtype>` variables are of type `Triple<Boolean, Int, Int>`. The Triple object is a wrapper for the following objects:
- **oneTime** (`Boolean`) 
	- defines if single payments are supported
- **lowerBound** (`Int`)
	- minimum number of installments
- **upperBound** (`Int`)
	- maximum number of installments

Attention needs to be paid to the following conditions:
- *lowerBound >= 0*
- *upperBound <= 99*
- *lowerBound <= upperBound*

&nbsp;
The `DynamicInstallmentParams.Builder()` object has several functions, each for defining available installment values for a certain card type. Each function has the following signature:
`fun add<cardtype>(oneTime: Boolean, lowerBound: Int, upperBound: Int) : DynamicInstallmentParams.Builder`
- Single payments are explicitly defined by setting the `oneTime` value to true, not by setting the `lowerBound` value to 1.
- Please note, when calling this function, an `IllegalArgumentException` can be thrown if any of the aforementioned conditions are not met.

After adding installment values for cards, the `build()` function needs to be called in order to construct the `DynamicInstallmentParams` object.
- Please note, if not a single `add<cardtype>` function was called before the `build()` function is called, a `CorvusSDKException` will be thrown with the following message: `"Dynamic payment data must be set for at least one card."`

**Note 1**: If a function for setting installment values for a certain card type is not called, that card will be disabled for payments.
**Note 2**: If installment values are defined multiples times for a certain card type, only the latest entry will be taken into account.
**Note 3:** In order to avoid the application crashing in case of a malformed `add<cardtype>()` function call, or if no installment values are added for any card type before calling `build()`, surround the calling code with a try-catch block.

&nbsp;
##### Usage example

**E.g.** `Available installment values for Visa`
```kotlin
val dynamicInstallmentParams = DynamicInstallmentParams.Builder()
	.addVisa(oneTime = true, lowerBound = 2, upperBound = 99)
	.build()

val installmentParams =
	InstallmentsParams.createUsingPaymentAllDynamicWith(dynamicInstallmentParams) 
```

&nbsp;
#### Payment All
This option enables specifying a flexible number of installments for all card types, which will be used during the checkout process.

In order to use the option, the following function of the `InstallmentParams` class is used:
`InstallmentParams.createUsingPaymentAllWith(oneTime: Boolean, lowerBound, Int, upperBound: Int): InstallmentParams`
- Single payments are explicitly defined by setting the `oneTime` value to true, not by setting the `lowerBound` value to 1.
- Please note, when calling this function, an `IllegalArgumentException` can be thrown if any of the following conditions are not met:
	- *lowerBound >= 0*
	- *upperBound <= 99*
	- *lowerBound <= upperBound*

**Note:** In order to avoid the application crashing in case of a malformed `createUsingPaymentAllWith()` function call, surround the calling code with a try-catch block.

&nbsp;
##### Usage example

**E.g.** 
```kotlin
val installmentParams = 
	InstallmentParams.createUsingPaymentAllWith(oneTime = true, lowerBound = 2, upperBound = 99)
```

  &nbsp;
#### Number Of Installments
This option enables specifying a fixed number of installments, which will be used during the checkout process. After setting it, when the buyer is redirected to the checkout screen, they will be able to only choose cards which support the set number of installments.

In order to use the option, the following function of the `InstallmentParams` class is used:
`InstallmentParams.createUsingNumberOfInstallmentsWith(numberOfInstallments: Int): InstallmentParams`
- Please note, when calling this function, an `IllegalArgumentException` can be thrown if the following condition is not met:
	- *numberOfInstallments >= 2 && numberOfInstallments <= 99*

**Note:** In order to avoid the application crashing in case of a malformed `createUsingNumberOfInstallmentsWith()` function call, surround the calling code with a try-catch block.

&nbsp;
##### Usage example

**E.g.** 
```kotlin
val installmentParams = 
	InstallmentsParams.createUsingNumberOfInstallmentsWith(numberOfInstallments = 2)
```

&nbsp;
# FAQ
List of possible exceptions messages and reasons for throwing, organized by class:

**CorvusPay**
- `CorvusSDKException: Order ID cannot be empty.`
	- Mandatory field `orderId` inside the Checkout object was left blank
- `CorvusSDKException: Cart cannot be empty.`
	- Mandatory field `cart` inside the Checkout object was left blank
 - `CorvusSDKException: Signature cannot be empty.`
	- Mandatory field `signature` inside the Checkout object was left blank
- `CorvusSDKException: Either installmentParams or installmentsMap object may be defined.`
	- Both installmentParams and installmentsMap were defined in the Checkout object
	- Please provide only one parameter
- `CorvusSDKException: BestBefore parameter has to be a valid future timestamp.`
	- Optional field `bestBefore` was provided as an outdated timestamp, must be a future timestamp
	- Please check your logic for calculating the timestamp, or don't define the parameter if you want to use the default value

**DynamicInstallmentParams**
- `CorvusSDKException: Dynamic payment data must be set for at least one card.`
	- No dynamic payment data was provided to the Builder of the `DynamicInstallmentParams` class before calling the `build()` function
- `IllegalArgumentException: Lower installment bound for <cardtype> must be 0 or greater.`
	- Argument `lowerBound` passed to function `DynamicInstallmentParams.Builder().add<cardtype>()` must be greater or equal to zero
- `IllegalArgumentException: Upper installment bound for <cardtype> must be 99 or lower.`
	- Argument `upperBound` passed to function `DynamicInstallmentParams.Builder().add<cardtype>()` must be lower or equal to 99
- `IllegalArgumentException: Lower bound must be lower or equal to upper installment bound for <cardtype>.`
	- Argument `upperBound` passed to function `DynamicInstallmentParams.Builder().add<cardtype>()` must be greater or equal to argument `lowerBound`

**InstallmentsMap**
- `CorvusSDKException: Card discount data must be set for at least one card.`
	- No card discount data was provided to the Builder of the `InstallmentsMap` class before calling the `build()` function
- `CorvusSDKException: At least one discount definition must be present.`
	- No discount definitions were provided to the Builder of the `InstallmentsMap.Discount` class before calling the `build()` function

**InstallmentParams**
- `IllegalArgumentException: Lower installment bound must be 0 or greater.`
	- Argument `lowerBound` passed to function `InstallmentParams.createUsingPaymentAllWith()` must be greater or equal to zero
- `IllegalArgumentException: Upper installment bound must be 99 or lower.`
	- Argument `upperBound` passed to function `InstallmentParams.createUsingPaymentAllWith()` must be lower or equal to 99
- `IllegalArgumentException: Lower bound must be lower or equal to upper installment bound.`
	- Argument `upperBound` passed to function `InstallmentParams.createUsingPaymentAllWith()` must be greater or equal to argument `lowerBound`
- `IllegalArgumentException: Number of installments value must be in 2..99 range.`
	- Argument `numberOfInstallments` passed to function `InstallmentParams.createUsingNumberOfInstallmentsWith()` must be between 2 and 99 (inclusive)
