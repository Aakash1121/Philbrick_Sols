package com.exampleble.ui.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.exampleble.R
import com.exampleble.common.ReadRequestConstants
import com.exampleble.common.ReusedMethod
import com.exampleble.common.ReusedMethod.Companion.hexToString
import com.exampleble.common.ReusedMethod.Companion.stringToHex
import com.exampleble.databinding.FragmentExtraConfigBinding
import com.exampleble.databinding.FragmentTripsBinding
import com.exampleble.ui.MobileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ExtraConfigFragment : BaseFragment() {
    lateinit var mBinding: FragmentExtraConfigBinding
    private lateinit var bleDevice: BleDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var gattService: BluetoothGattService
    private lateinit var gattCharacteristic: BluetoothGattCharacteristic
    private var isInitial = false
    private var rdGroup1SelectedId = "1"
    private var rdGroup2SelectedId = "1"
    private var rdGroup3SelectedId = "1"


    override fun getInflateResource(): Int {
        return R.layout.fragment_extra_config
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.incAppBar.txtDesc.text = "Extra Config"
        radioGrpListener1()
        radioGrpListener2()
        radioGrpListener3()

        bleDevice = (activity as MobileActivity).getBleDevice()!!

        gatt = BleManager.getInstance().getBluetoothGatt(bleDevice)

        openCloseNotification(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        openCloseNotification(false)
    }


    override fun postInit() {

    }

    override fun initObserver() {

    }
    fun radioGrpListener1() {
        mBinding.rdGroup1.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdYes1 -> {
                    rdGroup1SelectedId = "1"
                }
                R.id.rdNo1 -> {
                    rdGroup1SelectedId = "0"
                }
            }
        }
    }

    fun radioGrpListener2() {
        mBinding.rdGroup2.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdYes2 -> {
                    rdGroup2SelectedId = "1"
                }
                R.id.rdNo2 -> {
                    rdGroup2SelectedId = "0"
                }
            }
        }
    }
    fun radioGrpListener3() {
        mBinding.rdGroup3.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdYes3 -> {
                    rdGroup3SelectedId = "1"
                }
                R.id.rdNo3 -> {
                    rdGroup3SelectedId = "0"
                }
            }
        }
    }
    override fun handleListener() {
        mBinding.incAppBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        mBinding.btnSetConfig.setOnClickListener {
            
            val stopCancelRdGroup = "0$rdGroup1SelectedId"
            val attendedCopLightRdGroup = "0$rdGroup2SelectedId"
            val arrowStatusRdGroup = "0$rdGroup3SelectedId"

            val motorOverrunTimeoutConfigVal = "060310${stopCancelRdGroup}${attendedCopLightRdGroup}${arrowStatusRdGroup}EF"


            Log.i("HexStringWrite", motorOverrunTimeoutConfigVal.toString())

            writeData(motorOverrunTimeoutConfigVal) {
                Toast.makeText(requireContext(),
                    "Config Set Successfully",
                    Toast.LENGTH_SHORT).show()
            }
        }
        
    }

    override fun initProgressBar() {

    }

    override fun showLoadingIndicator(isShow: Boolean) {

    }

    private fun getBLEConfiguration(function: (Boolean) -> Unit) {
        val tempServiceUUID = UUID.fromString(ReadRequestConstants.SERVICE_UUID)
        for (service in gatt.services) {
            if (service.uuid == tempServiceUUID) {
                gattService = service
                break
            }
        }
        val tempCharUUID: UUID = UUID.fromString(ReadRequestConstants.CHAR_NOTIFICATION_UUID)
        for (character in gattService.characteristics) {
            if (character.uuid == tempCharUUID) {
                gattCharacteristic = character
                function(true)
                break
            }
        }
    }

    private fun writeData(hexString: String, function: (Boolean) -> Unit) {
        BleManager.getInstance().write(bleDevice,
            ReadRequestConstants.SERVICE_UUID,
            ReadRequestConstants.CHAR_WRITE_UUID,
            HexUtil.hexStringToBytes(hexString),
            object : BleWriteCallback() {

                override fun onWriteSuccess(
                    current: Int, total: Int, justWrite: ByteArray,
                ) {
                    function(true)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Write Successfully", Toast.LENGTH_SHORT)
                            .show()
                        if (!isInitial) {
                            isInitial = true
                            mBinding.initResponseVal.text =
                                HexUtil.formatHexString(justWrite).toString()
                        } else {
                            mBinding.writeResponseVal.text =
                                HexUtil.formatHexString(justWrite).toString()
                        }
                        Log.i("SuccessResponse", HexUtil.formatHexString(justWrite).toString())
                    }
                }

                override fun onWriteFailure(exception: BleException) {
                    function(false)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Read Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun openCloseNotification(showNotification: Boolean) {
        if (showNotification) {
            BleManager.getInstance().notify(bleDevice,
                ReadRequestConstants.SERVICE_UUID,
                ReadRequestConstants.CHAR_NOTIFICATION_UUID,
                object : BleNotifyCallback() {
                    override fun onNotifySuccess() {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(requireContext(), "Notify Success", Toast.LENGTH_SHORT)
                                .show()
                        }

                        readData()

//                    runOnUiThread(Runnable { addText(txt, "notify success") })
                    }

                    override fun onNotifyFailure(exception: BleException) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(requireContext(), "Notify Failure", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }


                    override fun onCharacteristicChanged(data: ByteArray) {

                        CoroutineScope(Dispatchers.Main).launch {

                            val hexString = HexUtil.formatHexString(data)
                            val rdGroupSelected1 ="${hexString[6]}${hexString[7]}"
                            val rdGroupSelected2 = "${hexString[8]}${hexString[9]}"
                            val rdGroupSelected3 = "${hexString[10]}${hexString[11]}"

                            if(rdGroupSelected1 == "01"){
                                mBinding.rdYes1.isChecked = true
                                mBinding.rdNo1.isChecked = false
                            }else{
                                mBinding.rdYes1.isChecked = false
                                mBinding.rdNo1.isChecked = true
                            }

                            if(rdGroupSelected2 == "01"){
                                mBinding.rdYes2.isChecked = true
                                mBinding.rdNo2.isChecked = false
                            }else{
                                mBinding.rdYes2.isChecked = false
                                mBinding.rdNo2.isChecked = true
                            }

                            if(rdGroupSelected3 == "01"){
                                mBinding.rdYes3.isChecked = true
                                mBinding.rdNo3.isChecked = false
                            }else{
                                mBinding.rdYes3.isChecked = false
                                mBinding.rdNo3.isChecked = true
                            }


                            mBinding.initResponseVal.text = hexString

                        }

                        /* runOnUiThread(Runnable {
                         addText(
                             txt,
                             HexUtil.formatHexString(
                                 characteristic.getValue(),
                                 true
                             )
                         )
                     })*/
                    }
                })
        } else {
            BleManager.getInstance().stopNotify(
                bleDevice,
                ReadRequestConstants.SERVICE_UUID,
                ReadRequestConstants.CHAR_NOTIFICATION_UUID,
            )
        }
    }

    private fun readData() {
        getBLEConfiguration {
            if (it) {
                writeData(ReadRequestConstants.ExtraConfig) { writeSuccess ->
                    if (writeSuccess) {
                        //openCloseNotification(true)
                    } else {
                        Toast.makeText(requireContext(),
                            "Can't Connect to Extra Config ",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}