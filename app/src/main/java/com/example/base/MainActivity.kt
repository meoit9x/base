package com.example.base

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.example.base.customView.ExtTextView
import com.example.base.data.AppPreferences
import com.example.base.databinding.ActivityMainBinding
import com.example.base.ui.home.HomeFragment
import com.example.base.ui.otherFragment.OtherFragment
import com.example.base.ui.setting.SettingFragment
import com.example.base.utils.MyContextWrapper

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val fragmentStates = ArrayList<String>()
    private var ft: FragmentTransaction? = null
    private val fragments: MutableList<Fragment> = ArrayList()
    private val btns: MutableList<ExtTextView> = ArrayList()
    private val fragmentHome: Fragment = HomeFragment()
    private var aFragment: Fragment? = null
    private var fragmentSetting: Fragment? = null

    private var currentTab: Int = 0
    private val TAB_HOME = 1
    private val TAB_LEVEL = 2
    private val TAB_SETTING = 3

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("keep_state", true)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    override fun onDestroy() {
        binding.bannerAdView.destroy()
        super.onDestroy()
    }

    fun setLoadingAdsView(visible: Boolean) {
        Log.d(TAG, "LoadingAdsView: " + visible)
        binding.loadingAdsLayout.loadingAdsLayout.bringToFront()
        binding.loadingAdsLayout.loadingAdsLayout.isVisible = visible
    }

    private fun initData() {
        showFragment(fragmentHome)
        enableButton(binding.btnHome)
        binding.btnHome.setOnClickListener { v ->
            if (currentTab != TAB_HOME) {
                currentTab = TAB_HOME
                if (fragmentHome.isAdded) {
                    showFragment(fragmentHome)
                    enableButton(binding.btnHome)
                }
            }
        }
        binding.btnLever.setOnClickListener { v ->
            if (currentTab != TAB_LEVEL) {
                currentTab = TAB_LEVEL

                if (aFragment == null) {
                    aFragment = OtherFragment()
                }
                if (!fragments.contains(aFragment)) {
                    fragments.add(aFragment!!)
                    aFragment!!::class.simpleName?.let { addFragment(aFragment!!, it) }
                }
                showFragment(aFragment!!)
                enableButton(binding.btnLever)
            }
        }
        binding.btnSetting.setOnClickListener { v ->
            if (currentTab != TAB_SETTING) {
                App.firebaseAnalytics.logEvent("SettingEvent",null)
                currentTab = TAB_SETTING
                if (fragmentSetting == null) {
                    fragmentSetting = SettingFragment()
                }
                if (!fragments.contains(fragmentSetting)) {
                    fragments.add(fragmentSetting!!)
                    fragmentSetting!!::class.simpleName?.let { addFragment(fragmentSetting!!, it) }
                }
                showFragment(fragmentSetting!!)
                enableButton(binding.btnSetting)
            }
        }

    }

    private fun enableButton(btnEnable: ExtTextView) {
        setTextViewDrawableColor(btnEnable, R.color.color_21C3FF)
        btnEnable.setTextColor(resources.getColor(R.color.color_21C3FF))
        for (i in btns.indices) {
            if (btns[i].id !== btnEnable.id) {
                setTextViewDrawableColor(btns[i], R.color.color_8D959D)
                btns[i].setTextColor(resources.getColor(R.color.color_8D959D))
            }
        }
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(textView.context, color),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }

    private fun initView() {
        fragments.add(fragmentHome)
        fragmentHome::class.simpleName?.let { addFragment(fragmentHome, it) }
        HomeFragment::class.simpleName?.let { fragmentStates.add(it) }
        btns.add(binding.btnHome)
        btns.add(binding.btnLever)
        btns.add(binding.btnSetting)
        
        initBannerAds()
    }

    //AppLovin Init Ads
    private fun initBannerAds() {
        binding.bannerAdView.setListener(object: MaxAdViewAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                Log.d(TAG, "onAdLoaded: ")
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                Log.d(TAG, "onAdDisplayed: ")
            }

            override fun onAdHidden(ad: MaxAd?) {
                Log.d(TAG, "onAdHidden: ")
            }

            override fun onAdClicked(ad: MaxAd?) {
                Log.d(TAG, "onAdClicked: ")
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.d(TAG, "onAdLoadFailed: ")
                Handler(Looper.getMainLooper()).postDelayed({ binding.bannerAdView.loadAd() }, 1000)
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.d(TAG, "onAdDisplayFailed: ")
                binding.bannerAdView.loadAd()
            }

            override fun onAdExpanded(ad: MaxAd?) {
                Log.d(TAG, "onAdExpanded: ")
            }

            override fun onAdCollapsed(ad: MaxAd?) {
                Log.d(TAG, "onAdCollapsed: ")
            }

        })
        binding.bannerAdView.setRevenueListener { Log.d(TAG, "onAdRevenuePaid: ") }
        binding.bannerAdView.loadAd()
        binding.bannerAdView.startAutoRefresh()
    }

    fun addFragment(fmAdd: Fragment, tag: String) {
        fragmentManager.executePendingTransactions()
        if (!fmAdd.isAdded) {
            if (fmAdd is HomeFragment) {
                HomeFragment::class.simpleName?.let { fragmentStates.add(it) }
            } else if (fmAdd is OtherFragment) {
                OtherFragment::class.simpleName?.let { fragmentStates.add(it) }
            } else if (fmAdd is SettingFragment) {
                SettingFragment::class.simpleName?.let { fragmentStates.add(it) }
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.frContent, fmAdd)
            transaction.commit()
        }
        hideOrShowBottomView(
            tag.contains(HomeFragment::class.java.simpleName) || tag.contains(
                OtherFragment::class.java.simpleName
            ) || tag.contains(SettingFragment::class.java.simpleName)
        )
    }

    private fun showFragment(fmShow: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.show(fmShow)
        fragmentManager.executePendingTransactions()
        for (i in fragments.indices) {
            hideFragment(fragments[i])
        }
        transaction.commit()
    }

    private fun hideFragment(fmHide: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fmHide)
        if (fmHide is HomeFragment) {
        }
        transaction.commit()
    }

    fun goHome(tag: String) {

        while (fragmentStates.size > 1) {
            fragmentStates.removeAt(fragmentStates.size - 1)
            supportFragmentManager.popBackStack()
        }
        showFragment(fragmentHome)
        enableButton(binding.btnHome)

        hideOrShowBottomView(
            tag.contains(HomeFragment::class.java.simpleName) || tag.contains(
                OtherFragment::class.java.simpleName
            ) || tag.contains(SettingFragment::class.java.simpleName)
        )
    }

    private fun hideOrShowBottomView(show: Boolean) {
        if (show) {
            binding.bannerAdView.visibility = View.VISIBLE
            if (binding.ctsBottomNavigation.visibility != View.VISIBLE)
                binding.ctsBottomNavigation.visibility = View.VISIBLE
        } else {
            binding.bannerAdView.visibility = View.GONE
            binding.ctsBottomNavigation.visibility = View.GONE
            binding.bannerAdView.visibility = View.GONE
        }
    }

    fun addChildFragment(fragment: Fragment, tag: String) {
        ft = supportFragmentManager.beginTransaction()
        ft!!.add(R.id.frContent, fragment)
        if (!fragmentStates.contains(tag)) fragmentStates.add(tag)
        ft!!.addToBackStack(tag)
        ft!!.commit()
        hideOrShowBottomView(
            tag.contains(HomeFragment::class.java.simpleName) || tag.contains(
                OtherFragment::class.java.simpleName
            ) || tag.contains(SettingFragment::class.java.simpleName)
        )
    }

    override fun onBackPressed() {
        if (fragmentStates.size > 1) {
            fragmentStates.removeAt(fragmentStates.size - 1)
            supportFragmentManager.popBackStack()
            val tagg = fragmentStates[fragmentStates.size - 1]
            hideOrShowBottomView(
                tagg.contains(HomeFragment::class.java.simpleName) || tagg.contains(
                    OtherFragment::class.java.simpleName
                ) || tagg.contains(SettingFragment::class.java.simpleName)
            )
            return
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (item in supportFragmentManager.fragments) {
            for (mItem in item.childFragmentManager.fragments)
                mItem.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (item in supportFragmentManager.fragments) {
            item.onActivityResult(resultCode, resultCode, data)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val language = AppPreferences.getCurrentLanguage()
        if (language != null)
            super.attachBaseContext(MyContextWrapper.wrap(newBase, language.value))
        else
            super.attachBaseContext(newBase)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}