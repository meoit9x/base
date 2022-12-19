package com.example.base.ui.setting

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.MainActivity
import com.example.base.R
import com.example.base.base.BaseFragment
import com.example.base.base.EmptyViewModel
import com.example.base.data.AppPreferences
import com.example.base.databinding.FragmentLanguageSettingBinding
import com.example.base.model.Language
import com.example.base.ui.onboard.language.LanguageAdapter
import com.example.base.utils.PreferenceUtil

class LanguageFragmentSetting : BaseFragment<FragmentLanguageSettingBinding, EmptyViewModel>() {
    override val viewModel: EmptyViewModel by lazy {
        ViewModelProvider(this)[EmptyViewModel::class.java]
    }

    private var listLanguage = mutableListOf<Language>()

    private var adapterLanguage: LanguageAdapter? = null

    private lateinit var currentLanguageSelected: Language

    override fun initData() {
        super.initData()
        AppPreferences.getCurrentLanguage()?.let {
            currentLanguageSelected = it
        } ?: kotlin.run {
            currentLanguageSelected =
                Language(getString(R.string.txt_language_en), true, "en", R.drawable.flag_en)
        }

        val english = PreferenceUtil.getString(context, PreferenceUtil.SETTING_ENGLISH, "")
        listLanguage.apply {
            add(
                Language(
                    getString(R.string.txt_language_en),
                    english.equals("en"),
                    "en",
                    R.drawable.flag_en
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_chi),
                    english.equals("za"),
                    "za",
                    R.drawable.flag_cn
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_ja),
                    english.equals("ja"),
                    "ja",
                    R.drawable.flag_jp
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_vi),
                    english.equals("vi"),
                    "vi",
                    R.drawable.flag_vi
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_spanish),
                    english.equals("es"),
                    "es",
                    R.drawable.flag_sp
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_portuguese),
                    english.equals("pt"),
                    "pt",
                    R.drawable.flag_ft
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_russiane),
                    false,
                    "ru",
                    R.drawable.flag_rs
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_korean),
                    english.equals("ko"),
                    "ko",
                    R.drawable.flag_kr
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_french),
                    english.equals("fr"),
                    "fr",
                    R.drawable.flag_fr
                )
            )
            add(
                Language(
                    getString(R.string.txt_language_german),
                    english.equals("de"),
                    "de",
                    R.drawable.flag_gr
                )
            )
        }
        AppPreferences.saveCurrentLanguage(currentLanguageSelected)
    }

    override fun initView() {
        super.initView()
        setupRecycleView()
    }

    override fun initEvent() {
        super.initEvent()
        binding.ivBack.setOnClickListener {
            backstackFragment()
        }
        binding.txtSave.setOnClickListener {
            PreferenceUtil.saveString(
                requireContext(),
                PreferenceUtil.SETTING_ENGLISH,
                currentLanguageSelected.value
            )
            AppPreferences.saveCurrentLanguage(currentLanguageSelected)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }, 500)
        }
    }

    private fun setupRecycleView() {
        adapterLanguage = LanguageAdapter(listLanguage, ::onLanguageSelected)
        binding.rcvEnglish.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterLanguage
        }
    }

    private fun onLanguageSelected(position: Int) {
        listLanguage.forEachIndexed { index, language ->
            if (language.isSelected) {
                language.isSelected = false
                adapterLanguage?.notifyItemChanged(index)
            }
        }

        listLanguage[position].isSelected = true
        currentLanguageSelected = listLanguage[position]
        adapterLanguage?.notifyItemChanged(position)
    }

}