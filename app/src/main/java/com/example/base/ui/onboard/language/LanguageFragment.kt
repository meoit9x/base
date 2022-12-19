package com.example.base.ui.onboard.language

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.example.base.App
import com.example.base.R
import com.example.base.adapter.LanguageAdpter
import com.example.base.adapter.LanguageAdpter.Language
import com.example.base.databinding.FragmentLanguageBinding
import com.example.base.ui.MyCustomOnboarder
import com.example.base.ui.splash.SplashActivity
import com.example.base.utils.PreferenceUtil
import java.util.*

class LanguageFragment() : Fragment(R.layout.fragment_language), Parcelable {
    private val TAG = "LanguageFragment"
    private var _binding: FragmentLanguageBinding? = null

    private val binding get() = _binding!!
    private var lstLanguage = ArrayList<Language>()
    private var adapter: LanguageAdpter? = null
    private var position: Int = -1
    private var lang: Language? = null
    private var firsTimeInApp = false

    // AppLovin SDK
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null
    private lateinit var nativeAdLayout: FrameLayout

    constructor(parcel: Parcel) : this() {
        position = parcel.readInt()
    }

    constructor(arg1: Boolean) : this() {
        firsTimeInApp = arg1;
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAds()
        initData()

        val language =
            PreferenceUtil.getString(requireContext(), PreferenceUtil.SETTING_ENGLISH, "")
        if (language.isNotEmpty()) {
            for (i in 0 until lstLanguage.size) {
                if (language == lstLanguage[i].values) {
                    position = i
                    lang = lstLanguage[position]
                    break
                }
            }
        }

        lang?.let {
            lstLanguage[position] = it
        }

        binding.save.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("language", lang?.language)
            App.firebaseAnalytics.logEvent("ChangeLanguage", bundle)
            PreferenceUtil.saveString(
                requireContext(),
                PreferenceUtil.SETTING_ENGLISH,
                lang?.values
            )
            val intent = Intent(requireActivity(), MyCustomOnboarder::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAdLoader.destroy(nativeAd)
        }
        nativeAdLoader.destroy()
        super.onDestroy()
    }

    private fun initAds() {
        if (activity is SplashActivity) {
            (activity as SplashActivity).showLoadingAds(true)
        }
        nativeAdLayout = requireActivity().findViewById(R.id.native_ad_layout)
        nativeAdLoader = MaxNativeAdLoader("YOUR_AD_UNIT", requireContext()) //Const.KEY_ADS_CHANGE_LANGUAGE
        nativeAdLoader.setRevenueListener { Log.d(TAG, "onAdRevenuePaid: ") }
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                Log.d(TAG, "onNativeAdLoaded: ")
                if (activity is SplashActivity) {
                    (activity as SplashActivity).showLoadingAds(false)
                }
                if (nativeAd != null) {
                    nativeAdLoader.destroy(nativeAd)
                }

                nativeAd = ad
                nativeAdLayout.removeAllViews()
                nativeAdLayout.addView(nativeAdView)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                Log.d(TAG, "onNativeAdLoadFailed: ")
                if (activity is SplashActivity) {
                    (activity as SplashActivity).showLoadingAds(false)
                }
            }

            override fun onNativeAdClicked(ad: MaxAd) {
                Log.d(TAG, "onNativeAdClicked: ")
            }
        })

        nativeAdLoader.loadAd()
        Handler(Looper.getMainLooper())
            .postDelayed({
                if (activity is SplashActivity) {
                    (activity as SplashActivity).showLoadingAds(false)
                }
            }, 5000)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        //set ui first time in app
        lstLanguage.add(Language(getString(R.string.txt_language_en), "en", R.drawable.flag_en))
        lstLanguage.add(Language(getString(R.string.txt_language_chi), "za", R.drawable.flag_cn))
        lstLanguage.add(Language(getString(R.string.txt_language_ja), "ja", R.drawable.flag_jp))
        lstLanguage.add(Language(getString(R.string.txt_language_vi), "vi", R.drawable.flag_vi))
        lstLanguage.add(
            Language(
                getString(R.string.txt_language_spanish),
                "es",
                R.drawable.flag_sp
            )
        )
        lstLanguage.add(
            Language(
                getString(R.string.txt_language_portuguese),
                "pt",
                R.drawable.flag_ft
            )
        )
        lstLanguage.add(
            Language(
                getString(R.string.txt_language_russiane),
                "ru",
                R.drawable.flag_rs
            )
        )
        lstLanguage.add(Language(getString(R.string.txt_language_korean), "ko", R.drawable.flag_kr))
        lstLanguage.add(Language(getString(R.string.txt_language_french), "fr", R.drawable.flag_fr))
        lstLanguage.add(Language(getString(R.string.txt_language_german), "de", R.drawable.flag_gr))

        if (firsTimeInApp) {
            PreferenceUtil.saveString(
                requireContext(),
                PreferenceUtil.SETTING_ENGLISH,
                lstLanguage[0].values
            )
        }


        adapter = LanguageAdpter(requireContext(), lstLanguage) {
            lang = it
            PreferenceUtil.saveString(
                requireContext(),
                PreferenceUtil.SETTING_ENGLISH,
                lang?.values
            )
            adapter!!.notifyDataSetChanged()
        }
        binding.rcvEnglish.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvEnglish.adapter = adapter
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LanguageFragment> {
        override fun createFromParcel(parcel: Parcel): LanguageFragment {
            return LanguageFragment(parcel)
        }

        override fun newArray(size: Int): Array<LanguageFragment?> {
            return arrayOfNulls(size)
        }
    }
}