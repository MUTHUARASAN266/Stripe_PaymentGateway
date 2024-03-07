package com.example.stripepoc

import com.stripe.Stripe
import com.stripe.android.core.exception.StripeException
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.PaymentMethodAttachParams

object StripeServices {

    // Set your secret key obtained from the Dashboard
    private const val SECRET_KEY =
        "sk_test_51Op20bSCcqOloxrmYKCvHQDnJCJUZUf6VZ6CYcCD8Qi4ix3am3gjvUWAIU0J9uzl6AG6Y5GM2djbjKPKh5CLEYL000H1Xftt0E"

    init {
        // Initialize the Stripe object with your API key
        Stripe.apiKey = SECRET_KEY
    }

    fun createPaymentIntent(amount: Long, currency: String, customerId: String): String {
        // Create a map to hold the parameters
//        val params = HashMap<String, Any>()
//        params["amount"] = amount
//        params["currency"] = currency
        // Add additional parameters if needed
        val params = PaymentIntentCreateParams.Builder()
            .setAmount(amount)
            .setCurrency(currency)
            .setReceiptEmail("km.arasankpk266@gamil.com")
            .setCustomer(customerId)
            .build()

        return try {
            // Create the PaymentIntent
            val paymentIntent = PaymentIntent.create(params)
            paymentIntent.clientSecret
        } catch (e: StripeException) {
            // Handle any errors
            e.printStackTrace()
            "" // or throw an exception
        }
    }
    fun attachPaymentMethodToCustomer(customerId: String, paymentMethodId: String): Boolean {
        return try {
            val params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build()

//            val paymentMethod = Stripe.paymentMethods.attach(
//                paymentMethodId,
//                params
//            )
            true
        } catch (e: StripeException) {
            e.printStackTrace()
            false
        }
    }
    fun createCustomer(name: String, email: String): String? {
        val params = CustomerCreateParams.builder()
            .setName(name)
            .setEmail(email)
            .setPhone("8220189947") // This line sets the phone number
            .build()

        return try {
            val customer = Customer.create(params)
            customer.id
        } catch (e: StripeException) {
            e.printStackTrace()
            null
        }
    }

}