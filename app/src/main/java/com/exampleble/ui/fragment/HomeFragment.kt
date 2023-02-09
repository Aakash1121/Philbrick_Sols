package com.exampleble.ui.fragment

import androidx.navigation.fragment.findNavController
import com.exampleble.R
import com.exampleble.databinding.FragmentHomeBinding
import com.exampleble.ui.MobileActivity

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun getInflateResource(): Int {
        return R.layout.fragment_home
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        mBinding = getBinding()

        mBinding.btnBluetooth.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mainFragment1)
        }
        if ((activity as MobileActivity).isBLEConnected) {
            mBinding.btnFloorDisplaying.isEnabled = true
            mBinding.btnBasicConfig.isEnabled = true
            mBinding.btnAutoDoorConfig.isEnabled = true
            mBinding.btnDoorPermission.isEnabled = true
            mBinding.btnTrips.isEnabled = true
            mBinding.btnMotorOverrunTimeout.isEnabled = true
            mBinding.btnExtraConfig.isEnabled = true


            mBinding.btnFloorDisplaying.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_floorDisplayConfigFragment)
            }

            mBinding.btnBasicConfig.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_basicConfigFragment)
            }

            mBinding.btnAutoDoorConfig.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_autoDoorConfigFragment)
            }

            mBinding.btnDoorPermission.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_doorPermissionFragment)
            }

            mBinding.btnTrips.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_tripsFragment)
            }

            mBinding.btnMotorOverrunTimeout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_motorConfigFragment)
            }

            mBinding.btnExtraConfig.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_extraConfigFragment)
            }

        }

    }

    private fun navigateToFragment(
        fragmentManager: androidx.fragment.app.FragmentManager,
        yourPlaceholder: Int,
        fragment: androidx.fragment.app.Fragment,
        addToBackStack: Boolean,
    ) {
        val transaction =
            fragmentManager.beginTransaction()
        transaction.replace(yourPlaceholder, fragment)
        if (addToBackStack) transaction.addToBackStack("")
        transaction.commit()
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