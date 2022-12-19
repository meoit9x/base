package org.app.admob

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinSdk

object AdMobSdk {
  private const val TAG = "AdMobSdk"

  fun initializeSdk(context: Context, callback: (() -> Unit)? = null) {
    // Make sure to set the mediation provider value to "max" to ensure proper functionality
    AppLovinSdk.getInstance(context).mediationProvider = "max"
    AppLovinSdk.getInstance(context).initializeSdk {
      Log.d(TAG, "initializeSdk complete!!!")
      callback?.invoke()
    }
  }

  fun showDebugView(context: Context) {
    AppLovinSdk.getInstance(context).showMediationDebugger()
  }
}