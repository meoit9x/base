package com.example.base.ui.setting;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.base.BaseFragment;
import com.example.base.databinding.FragmentPrivacyBinding;
import com.example.base.ui.home.HomeViewModel;

public class PrivacyFragment extends BaseFragment<FragmentPrivacyBinding, HomeViewModel> {
    private static final String TAG = "PrivacyFragment";

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

//        loadRewardAds();
    }

//    @Override
//    public void onAdsViewStateChanged(@NonNull ADS_TYPE type, @NonNull ADS_STATE state) {
//        if (type == ADS_TYPE.REWARD) {
//            switch (state) {
//                case LOADED:
//                    showRewardVideo();
//                    break;
//                default:
//                    Log.d(TAG, "onAdsViewStateChanged: " + state);
//            }
//        }
//        super.onAdsViewStateChanged(type, state);
//    }

    @Override
    public void initData() {
        super.initData();
    }

}
