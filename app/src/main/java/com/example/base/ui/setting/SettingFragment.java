package com.example.base.ui.setting;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.MainActivity;
import com.example.base.base.BaseFragment;
import com.example.base.databinding.FragmentSettingBinding;
import com.example.base.ui.home.HomeViewModel;

public class SettingFragment extends BaseFragment<FragmentSettingBinding, HomeViewModel> {

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public void initView() {
        super.initView();
        binding.clAboutApp.setOnClickListener(v -> ((MainActivity) getActivity()).addChildFragment(new AboutAppFragment(), AboutAppFragment.class.getSimpleName()));
        binding.clLanguage.setOnClickListener(v -> ((MainActivity) getActivity()).addChildFragment(new LanguageFragmentSetting(), LanguageFragmentSetting.class.getSimpleName()));
        binding.clPrivacy.setOnClickListener(v -> ((MainActivity) getActivity()).addChildFragment(new PrivacyFragment(), PrivacyFragment.class.getSimpleName()));
//        binding.appLovinDebug.setOnClickListener(v -> {
//            AppLovinSdk.getInstance(getContext()).showMediationDebugger();
//        });
    }

    @Override
    public void initData() {
        super.initData();
    }
}
