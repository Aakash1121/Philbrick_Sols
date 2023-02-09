package com.exampleble.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.exampleble.R
import com.exampleble.databinding.FragmentDoorPermissionBinding

class DoorPermissionFragment : BaseFragment() {
    lateinit var mBinding:FragmentDoorPermissionBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_door_permission
    }

    override fun displayMessage(message: String) {
        
    }

    override fun initView() {
        mBinding=getBinding()

    }

    override fun postInit() {
        
    }

    override fun initObserver() {
        
    }

    override fun handleListener() {
        mBinding.incAppBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.btnFrontDoorConfig.setOnClickListener {
            findNavController().navigate(R.id.action_doorPermissionFragment_to_frontDoorPermissionFragment)
        }
        mBinding.btnRearDoorConfig.setOnClickListener {
            findNavController().navigate(R.id.action_doorPermissionFragment_to_rearDoorPermissionsFragment)
        }
    }

    override fun initProgressBar() {
        
    }

    override fun showLoadingIndicator(isShow: Boolean) {
        
    }


}


