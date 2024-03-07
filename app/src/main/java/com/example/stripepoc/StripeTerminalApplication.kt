package com.example.stripepoc

import android.app.Application
import com.stripe.android.PaymentConfiguration

class StripeTerminalApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            getString(R.string.stripe_publishable_key)
        )
    }
}
