package com.example.base.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.base.MainActivity
import com.example.base.databinding.ActivitySplashBinding
import com.example.base.ui.MyCustomOnboarder
import com.example.base.ui.onboard.language.LanguageFragment
import com.example.base.utils.PreferenceUtil
import com.example.base.utils.setupSystemWindowInset

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemWindowInset()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstTime = PreferenceUtil.getBoolean(applicationContext, PreferenceUtil.OPEN_APP_FIRST_TIME, true)
        if (firstTime) {
            var delay = 700L
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                binding.languageContainer.viewTreeObserver.addOnDrawListener { false }
                delay = 0
            }
            Handler(Looper.getMainLooper()).postDelayed({
                val language = PreferenceUtil.getString(baseContext, PreferenceUtil.SETTING_ENGLISH, "")
                if ("" == language) {
                    supportFragmentManager.beginTransaction().add(binding.languageContainer.id, LanguageFragment(true)).commit()
                } else {
                    val intent = Intent(this, MyCustomOnboarder::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }, delay)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    fun showLoadingAds(visible: Boolean) {
        binding.languageLoadingAdsLayout.bringToFront()
        binding.languageLoadingAdsLayout.isVisible = visible
    }
}