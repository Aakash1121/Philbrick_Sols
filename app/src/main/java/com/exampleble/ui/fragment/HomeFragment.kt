package com.exampleble.ui.fragment

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.exampleble.R
import com.exampleble.common.ReusedMethod
import com.exampleble.databinding.FragmentHomeBinding
import com.exampleble.ui.MobileActivity
import com.exampleble.ui.bluetoothfragment.MainFragment1

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun getInflateResource(): Int {
        return R.layout.fragment_home
    }

    override fun displayMessage(message: String) {

    }

    override fun onResume() {
        super.onResume()

        if (!(activity as MobileActivity).isBLEConnected) {
            mBinding.btnFloorDisplaying.isEnabled = false
            mBinding.btnBasicConfig.isEnabled = false
            mBinding.btnAutoDoorConfig.isEnabled = false
            mBinding.btnDoorPermission.isEnabled = false
            mBinding.btnTrips.isEnabled = false
            mBinding.btnMotorOverrunTimeout.isEnabled = false
            mBinding.btnExtraConfig.isEnabled = false


        }
    }

    override fun initView() {
        mBinding = getBinding()


        mBinding.btnBluetooth.setOnClickListener {
            ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                R.id.nav_host_fragment,
                MainFragment1(),true)
//            findNavController().navigate(R.id.action_homeFragment_to_mainFragment1)
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
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    FloorDisplayConfigFragment(),true)
//            findNavController().navigate(R.id.action_homeFragment_to_floorDisplayConfigFragment)
            }


            mBinding.btnBasicConfig.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    BasicConfigFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_basicConfigFragment)
            }

            mBinding.btnAutoDoorConfig.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    AutoDoorConfigFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_autoDoorConfigFragment)
            }

            mBinding.btnDoorPermission.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    DoorPermissionFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_doorPermissionFragment)
            }

            mBinding.btnTrips.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    TripsFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_tripsFragment)
            }

            mBinding.btnMotorOverrunTimeout.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    MotorConfigFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_motorConfigFragment)
            }

            mBinding.btnExtraConfig.setOnClickListener {
                ReusedMethod.navigateToFragment(requireActivity().supportFragmentManager,
                    R.id.nav_host_fragment,
                    ExtraConfigFragment(),true)
//                findNavController().navigate(R.id.action_homeFragment_to_extraConfigFragment)
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