package com.example.base.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.base.BaseFragment;
import com.example.base.databinding.FragmentHomeBinding;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private static final String TAG = "HomeFragment";

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public void initObserver() {
        super.initObserver();
    }

    @Override
    public void initData() {
        super.initData();

    }
}
