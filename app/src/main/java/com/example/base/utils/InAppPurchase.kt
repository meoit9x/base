package com.example.base.utils

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.android.billingclient.api.*
import com.example.base.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

class InAppPurchase private constructor(
    application: Application,
    knownInAppSKUs: Array<String>?
) : PurchasesUpdatedListener, BillingClientStateListener {

    private val defaultScope = GlobalScope
    private val billingClient: BillingClient

    private val knownInAppSKUs: List<String>?

    private val skuStateMap: MutableMap<String, MutableStateFlow<SkuState>> = HashMap()
    private val skuDetailsMap: MutableMap<String, MutableStateFlow<SkuDetails?>> = HashMap()

    private enum class SkuState {
        SKU_STATE_UNPURCHASED, SKU_STATE_PENDING, SKU_STATE_PURCHASED, SKU_STATE_PURCHASED_AND_ACKNOWLEDGED
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                defaultScope.launch {
                    querySkuDetailsAsync()
                    restorePurchases()
                }
            }
        }
    }

    private fun addSkuFlows(skuList: List<String>?) {
        if (null == skuList) {
            Log.e(TAG, "addSkuFlows: SkuList is either null or empty.")
        }
        for (sku in skuList!!) {
            val skuState = MutableStateFlow(SkuState.SKU_STATE_UNPURCHASED)
            val details = MutableStateFlow<SkuDetails?>(null)
            // this initialization calls querySkuDetailsAsync() when the first subscriber appears
            details.subscriptionCount.map { count ->
                count > 0
            } // map count into active/inactive flag
                .distinctUntilChanged() // only react to true<->false changes
                .onEach { isActive -> // configure an action
                    if (isActive) {
                        querySkuDetailsAsync()
                    }
                }
                .launchIn(defaultScope)

            skuStateMap[sku] = skuState
            skuDetailsMap[sku] = details
        }
    }

    private suspend fun querySkuDetailsAsync() {
        if (!knownInAppSKUs.isNullOrEmpty()) {
            val skuDetailsResult = billingClient.querySkuDetails(
                SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.INAPP)
                    .setSkusList(knownInAppSKUs.toMutableList())
                    .build()
            )

            onSkuDetailsResponse(skuDetailsResult.billingResult, skuDetailsResult.skuDetailsList)
        }
    }

    private suspend fun restorePurchases() {
        val purchasesResult = billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP)
        val billingResult = purchasesResult.billingResult
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            handlePurchase(purchasesResult.purchasesList)
        }
    }

    private fun onSkuDetailsResponse(billingResult: BillingResult, skuDetailsList: List<SkuDetails>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                if (skuDetailsList == null || skuDetailsList.isEmpty()) {
                    Log.e(TAG, "onSkuDetailsResponse: Found null or empty SkuDetails. ")
                } else {
                    for (skuDetails in skuDetailsList) {
                        val sku = skuDetails.sku
                        val detailsMutableFlow = skuDetailsMap[sku]
                        detailsMutableFlow?.tryEmit(skuDetails) ?: Log.e(TAG, "Unknown sku: $sku")
                    }
                }
            }
        }
    }

    fun launchBillingFlow(activity: Activity, sku: String) {
        val skuDetails = skuDetailsMap[sku]?.value
        if (null != skuDetails) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        }
        Log.e(TAG, "SkuDetails not found for: $sku")
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> if (null != list) {
                handlePurchase(list)
                return
            } else Log.d(TAG, "Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(TAG, "onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Log.i(TAG, "onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(TAG, "onPurchasesUpdated: Does not recognize the configuration")
            else -> Log.d(TAG, "BillingResult [" + billingResult.responseCode + "]: " + billingResult.debugMessage)
        }
    }

    private fun handlePurchase(purchases: List<Purchase>?) {
        if (null != purchases) {
            for (purchase in purchases) {
                val purchaseState = purchase.purchaseState
                if (purchaseState == Purchase.PurchaseState.PURCHASED){
                    if (!isSignatureValid(purchase)) {
                        Log.e(TAG, "Invalid signature. Check to make sure your public key is correct.")
                        continue
                    }
                    setSkuStateFromPurchase(purchase)

                    if (!purchase.isAcknowledged) {
                        defaultScope.launch {
                            for (sku in purchase.skus) {
                                // Acknowledge item and change its state
                                val billingResult = billingClient.acknowledgePurchase(
                                    AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.purchaseToken)
                                        .build()
                                )
                                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                                    Log.e(TAG, "Error acknowledging purchase: ${purchase.skus}")
                                } else {
                                    // purchase acknowledged
                                    val skuStateFlow = skuStateMap[sku]
                                    skuStateFlow?.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                                }
                            }
                        }
                    }
                }  else {
                    // Not purchased
                    setSkuStateFromPurchase(purchase)
                }
            }
        } else {
            Log.d(TAG, "Empty purchase list.")
        }
    }

    private fun setSkuStateFromPurchase(purchase: Purchase) {
        if (purchase.skus.isNullOrEmpty()) {
            Log.e(TAG, "Empty list of skus")
            return
        }

        for (sku in purchase.skus) {
            val skuState = skuStateMap[sku]
            if (null == skuState) {
                Log.e(TAG, "Unknown SKU " + sku )
                continue
            }

            when (purchase.purchaseState) {
                Purchase.PurchaseState.PENDING -> skuState.tryEmit(SkuState.SKU_STATE_PENDING)
                Purchase.PurchaseState.UNSPECIFIED_STATE -> skuState.tryEmit(SkuState.SKU_STATE_UNPURCHASED)
                Purchase.PurchaseState.PURCHASED -> if (purchase.isAcknowledged) {
                    skuState.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                } else {
                    skuState.tryEmit(SkuState.SKU_STATE_PURCHASED)
                }
                else -> Log.e(TAG, "Purchase in unknown state: " + purchase.purchaseState)
            }
        }
    }

    fun isPurchased(sku: String): Flow<Boolean> {
        val skuStateFLow = skuStateMap[sku]!!
        return skuStateFLow.map { skuState -> skuState == SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED }
    }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        if (TextUtils.isEmpty(purchase.originalJson) || TextUtils.isEmpty(PUBLIC_KEY)
            || TextUtils.isEmpty(purchase.signature)) {
            Log.w(TAG, "Purchase verification failed: missing data.")
            return false
        }
        return try {
            val key = generatePublicKey(PUBLIC_KEY)
            verify(key, purchase.originalJson, purchase.signature)
        } catch (e: IOException) {
            Log.e(TAG, "Error generating PublicKey from encoded key: " + e.message)
            false
        }
    }

    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String): PublicKey {
        return try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            Log.w(TAG, msg)
            throw IOException(msg)
        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String?): Boolean {
        val signatureBytes: ByteArray = try {
            Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Base64 decoding failed.")
            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Log.w(TAG, "Signature verification failed...")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "Invalid key specification.")
        } catch (e: SignatureException) {
            Log.e(TAG, "Signature exception.")
        }
        return false
    }

    fun getSkuTitle(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.title
        }
    }

    fun getSkuPrice(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.price
        }
    }

    fun getSkuDescription(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.description
        }
    }

    override fun onBillingServiceDisconnected() {
        //TODO: Reconnect the service
        Log.i(TAG, "Service disconnected")
    }

    init {
        this.knownInAppSKUs = if (knownInAppSKUs == null) {
            ArrayList()
        } else {
            listOf(*knownInAppSKUs)
        }

        //Add flow for in app purchases
        addSkuFlows(this.knownInAppSKUs)

        //Connecting the billing client
        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(this)
    }

    companion object {
        private val TAG = InAppPurchase::class.java.simpleName

        const val PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwXELWf46yrUsotLeG+JP4TMnY9L1BH6I5m5D0WQhjC6pbHNZQtKyX8wID9AKYY5fgbS+FvWjUU7BrDlAYQ3QeuRdp/n+0Dv4LuTetF0vJrB9MuuZqpo0V5N6tI+xuLf46L7PphwKAMD6T660zuEtS7RSykW+vgM+LFxUQ+o915PE3f+PwN4uODkd1ATl8u5H0hU2GL/zXWNjbgiyEUjESj1qoc+IpChEzhBWxo9yfw6OWlGtItSepCrIZYLLm92zKufIOXUPDSpaugmx7FZhYO55YpT4GTPP8IJDi6ZOsWqDbaDvtSt2DCuuyQ5JigYIPu4zNNk+Cqhn1iYHLBeEVwIDAQAB"
        private const val KEY_FACTORY_ALGORITHM = "RSA"
        private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

        // Define product list
        const val IN_APP_PROD_1 = "dc3iqn101"

        @Volatile
        private var sharedInstance: InAppPurchase? = null

        @JvmStatic
        fun initialize(application: Application) {
            if (sharedInstance == null) {
                sharedInstance = InAppPurchase(application, arrayOf(IN_APP_PROD_1))
            }
        }

        @JvmStatic
        fun getInstance() = sharedInstance ?: synchronized(this) {
            sharedInstance ?: InAppPurchase(
                App.getInstance(),
                arrayOf(
                    IN_APP_PROD_1
                ),
            ).also { sharedInstance = it }
        }
    }
}