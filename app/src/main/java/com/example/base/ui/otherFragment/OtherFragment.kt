package com.example.base.ui.otherFragment

import androidx.lifecycle.ViewModelProvider
import com.example.base.base.BaseFragment
import com.example.base.base.EmptyViewModel
import com.example.base.databinding.FragmentOtherBinding

class OtherFragment : BaseFragment<FragmentOtherBinding, EmptyViewModel>(){
    override val viewModel: EmptyViewModel by lazy {
        ViewModelProvider(this)[EmptyViewModel::class.java]
    }
}