package com.example.base

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.example.base.log.DebugTree
import com.example.base.log.ReleaseTree
import com.facebook.ads.AdSettings
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.app.admob.AdMobSdk
import timber.log.Timber

class App : Application() {
    private val TAG = "DictionaryApp"

    override fun onCreate() {
        super.onCreate()
        instance = this
        initTimber()

        // Initialize Admob SDK
        AdMobSdk.initializeSdk(this) {
            Log.d(TAG, "Admob SDK initialized!!")
        }

        AdSettings.setDataProcessingOptions( arrayOf<String>() )
        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX
        AppLovinSdk.getInstance(this).initializeSdk {
            Log.d(TAG, "onCreate: ")
        }
        val bundle = Bundle()
        bundle.putString("app_open", "app_open")
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    companion object {
        @Volatile
        private var instance: App? = null

        @JvmStatic
        fun getInstance(): App = instance ?: synchronized(this) {
            instance ?: App().also {
                instance = it
            }
        }
        lateinit var firebaseAnalytics: FirebaseAnalytics
    }
}