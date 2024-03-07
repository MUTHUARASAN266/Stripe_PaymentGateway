package com.example.stripepoc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.core.exception.StripeException
import com.stripe.android.model.Address
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.DelicateCardDetailsApi
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.CardInputWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var stripe: Stripe
    private lateinit var cardInputWidget: CardInputWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        val payButton: Button = findViewById(R.id.btn_pay)
        payButton.setOnClickListener {
            // Call the handlePayment function with the desired amount
            handlePayment(1000L, "inr") // For example, 1000L represents $10
        }
        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this@MainActivity, StripeScreen::class.java))


        }
    }

    private fun initializeViews() {
        stripe = Stripe(
            this,
            getString(R.string.stripe_publishable_key)
        )
        cardInputWidget = findViewById(R.id.cardInputWidget)
        cardInputWidget.postalCodeEnabled = false
    }

    @SuppressLint("RestrictedApi")
    @OptIn(DelicateCardDetailsApi::class)
    private fun handlePayment(amount: Long, currency: String) {

        val card = cardInputWidget.cardParams ?: run {
            showToast("Card details are null.")
            return
        }

        val billingDetails = PaymentMethod.BillingDetails(
            name = "muhtu",
            email = "arasankpk@gmail.com",
            phone = "8220189947",
            address = Address(
                line1 = "1234 Market St",
                city = "Chennai",
                country = "IN",
                postalCode = "614602",
                state = "Tamilnadu"
            )
        )
        val shippingDetails = ConfirmPaymentIntentParams.Shipping(
            name = "arasan Rosen",
            address = Address.Builder()
                .setLine1("1234 Market St")
                .setCity("San Francisco")
                .setState("CA")
                .setCountry("US")
                .setPostalCode("94111")
                .build()
        )

        val paymentMethodCreateParams = PaymentMethodCreateParams.create(
            card = PaymentMethodCreateParams.Card(
                number = card.number,
                expiryMonth = card.expMonth,
                expiryYear = card.expYear,
                cvc = card.cvc
            ),
            billingDetails = billingDetails
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerId = StripeServices.createCustomer("John Doe", "john.doe@example.com")
                val paymentMethodId =
                    stripe.createPaymentMethodSynchronous(paymentMethodCreateParams)?.id

                if (customerId != null) {
                    if (paymentMethodId != null) {
                        // Create a PaymentIntent with the customer, payment method, and setup_future_usage
                        val clientSecret =
                            StripeServices.createPaymentIntent(amount, currency, customerId)
                        val params = ConfirmPaymentIntentParams.createWithPaymentMethodId(
                            paymentMethodId,
                            clientSecret
                        )
                        params.setupFutureUsage =
                            ConfirmPaymentIntentParams.SetupFutureUsage.OnSession
                        params.shipping = shippingDetails

                        // Confirm the PaymentIntent
                        confirmPayment(params)
                        Log.d(TAG, "Payment confirmed successfully.")
                        showToast("Payment confirmed successfully")
                    } else {
                        showToast("Failed to create payment method")
                    }
                } else {
                    showToast("Failed to create customer")
                }
            } catch (e: StripeException) {
                Log.e(TAG, "Stripe Exception: ${e.message}")
                showToast("Error: ${e.message}")
            }
        }
    }


    private fun confirmPayment(params: ConfirmPaymentIntentParams) {
        try {
            stripe.confirmPayment(this@MainActivity, params)
        } catch (e: StripeException) {
            Log.e(TAG, "Error confirming payment: ${e.message}")
            // Handle confirm payment error
            showToast("Error confirming payment: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            stripe.onPaymentResult(requestCode, data,
                object : ApiResultCallback<PaymentIntentResult> {
                    override fun onError(e: Exception) {
                        Log.e(TAG, "onError: ${e.message}")
                    }

                    override fun onSuccess(result: PaymentIntentResult) {
                        val status = result.intent.status
                        if (status == StripeIntent.Status.Succeeded) {
                            // Payment succeeded
                            showToast("Payment succeeded")
                            Log.e(TAG, "onSuccess: $result")
                        } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                            // Payment failed due to insufficient funds, card declined, etc.
                            showToast("Payment failed: ${result.intent.lastPaymentError?.message}")
                            Log.e(
                                TAG,
                                "lastPaymentError: ${result.intent.lastPaymentError?.message}"
                            )
                            Log.e(TAG, "lastErrorMessage: ${result.intent.lastErrorMessage}")
                            Log.e(TAG, "cancellationReason: ${result.intent.cancellationReason}")
                        }
                    }

                })

        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

