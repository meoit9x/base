package com.example.base.ui.setting;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.base.BaseFragment;
import com.example.base.databinding.FragmentAboutAppBinding;
import com.example.base.databinding.FragmentLanguageSettingBinding;
import com.example.base.ui.home.HomeViewModel;

public class AboutAppFragment extends BaseFragment<FragmentAboutAppBinding, HomeViewModel> {

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public void initView() {
        super.initView();
        binding.ivBack.setOnClickListener(v -> {
            backstackFragment();
        });
    }

    @Override
    public void initData() {
        super.initData();
    }

}
