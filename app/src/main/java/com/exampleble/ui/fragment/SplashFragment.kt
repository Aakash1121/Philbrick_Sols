package com.exampleble.ui.fragment

import android.os.Handler
import androidx.navigation.fragment.findNavController
import com.exampleble.R
import com.exampleble.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment() {
    private lateinit var mBinding: FragmentSplashBinding

    override fun getInflateResource(): Int {
        return R.layout.fragment_splash
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        Handler().postDelayed(
            { findNavController().navigate(R.id.action_splashFragment_to_homeFragment) },
            1000
        )
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun initProgressBar() {

    }

    override fun showLoadingIndicator(isShow: Boolean) {

    }


}