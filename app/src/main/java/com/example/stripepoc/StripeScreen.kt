package com.example.stripepoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stripepoc.databinding.ActivityStripeScreenBinding
import com.stripe.Stripe
import com.stripe.android.view.CardInputWidget
import com.stripe.exception.StripeException
import com.stripe.model.Charge
import com.stripe.param.ChargeCreateParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StripeScreen : AppCompatActivity() {
    private lateinit var cardInputWidget: CardInputWidget

    private lateinit var binding: ActivityStripeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityStripeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Stripe.apiKey = getString(R.string.stripe_client_secret)
        cardInputWidget = findViewById(R.id.cardInputWidget)
        binding.btnPayment.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        // Get card details from your UI elements
        val cardNumber = "4242424242424242"
        val expMonth = 12
        val expYear = 2025
        val cvc = "123"

        // Create a Stripe token with the card details
     //   val token = com.stripe.android.Stripe.getToken(cardNumber, expMonth, expYear, cvc)

        // Charge the customer with the token
       // chargeCustomer(token.id)
    }

    private fun chargeCustomer(tokenId: String) {
        CoroutineScope(Dispatchers.IO).launch{
            val chargeParams = ChargeCreateParams.builder()
                .setAmount(1000) // Amount in cents
                .setCurrency("inr") // Set currency to Indian Rupee
                .setSource(tokenId)
                .build()

            try {
                val charge: Charge = Charge.create(chargeParams)
                // Handle successful charge
                runOnUiThread {
                    // Show success message or navigate to a success screen
                }
            } catch (e: StripeException) {
                // Handle Stripe errors
                runOnUiThread {
                    // Show error message to the user
                }
                e.printStackTrace()
            }
        }
    }
}