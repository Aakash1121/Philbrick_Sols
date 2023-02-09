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
import com.exampleble.common.ReusedMethod.Companion.decimalToHex
import com.exampleble.common.ReusedMethod.Companion.hexToDecimal
import com.exampleble.common.ReusedMethod.Companion.hexToString
import com.exampleble.common.ReusedMethod.Companion.largeDecimalToHex
import com.exampleble.common.ReusedMethod.Companion.stringToHex
import com.exampleble.databinding.FragmentAutoDoorConfigBinding
import com.exampleble.ui.MobileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AutoDoorConfigFragment : BaseFragment() {
    lateinit var mBinding: FragmentAutoDoorConfigBinding
    private lateinit var bleDevice: BleDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var gattService: BluetoothGattService
    private lateinit var gattCharacteristic: BluetoothGattCharacteristic
    private var isInitial = false


    override fun getInflateResource(): Int {
        return R.layout.fragment_auto_door_config
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.incAppBar.txtDesc.text = "Auto Door Config"
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

    override fun handleListener() {
        mBinding.incAppBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.btnSetConfig.setOnClickListener {

            when (ReusedMethod.editTextChecker(requireContext(),
                "Door Operation Timeout",
                mBinding.edtDoorOperationTimeOut.text.toString())
                    && ReusedMethod.editTextChecker(requireContext(),
                    "Hold Time",
                    mBinding.edtHoldTime.text.toString())
            ) {

                true -> {
                    val doorOperationTimeOutVal = largeDecimalToHex(mBinding.edtDoorOperationTimeOut.text.toString())

                    val holdTimeVal = largeDecimalToHex(mBinding.edtHoldTime.text.toString())

                    val autoDoorConfigVal = "090303${doorOperationTimeOutVal}${holdTimeVal}EF"

                    Log.i("HexStringWrite", autoDoorConfigVal.toString())

                    writeData(autoDoorConfigVal) {
                        Toast.makeText(requireContext(),
                            "Config Set Successfully",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
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

                            val doorOperationTimeOutVal = "${hexString[7]}${hexString[9]}${hexString[11]}"
                            mBinding.edtDoorOperationTimeOut.setText(doorOperationTimeOutVal)

                            val holdTimeVal = "${hexString[13]}${hexString[15]}${hexString[17]}"
                            mBinding.edtHoldTime.setText(holdTimeVal)

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
                writeData(ReadRequestConstants.AutoDoorConfig) { writeSuccess ->
                    if (writeSuccess) {
                        //openCloseNotification(true)
                    } else {
                        Toast.makeText(requireContext(),
                            "Can't Connect to Auto Door Config ",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}