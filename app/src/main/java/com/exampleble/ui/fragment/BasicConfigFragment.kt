package com.exampleble.ui.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
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
import com.exampleble.common.ReusedMethod.Companion.editTextChecker
import com.exampleble.common.ReusedMethod.Companion.decimalToHex
import com.exampleble.common.ReusedMethod.Companion.hexToDecimal
import com.exampleble.common.ReusedMethod.Companion.hexToString
import com.exampleble.common.ReusedMethod.Companion.largeDecimalToHex
import com.exampleble.common.ReusedMethod.Companion.stringToHex
import com.exampleble.databinding.FragmentBasicConfigBinding
import com.exampleble.ui.MobileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*


class BasicConfigFragment : BaseFragment() {

    private lateinit var mBinding: FragmentBasicConfigBinding
    private lateinit var bleDevice: BleDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var gattService: BluetoothGattService
    private lateinit var gattCharacteristic: BluetoothGattCharacteristic
    private var isInitial = false

    override fun getInflateResource(): Int {
        return R.layout.fragment_basic_config
    }

    override fun displayMessage(message: String) {
    }

    override fun initView() {

        mBinding = getBinding()
        mBinding.incAppBar.txtDesc.text = "Basic Config"
        bleDevice = (activity as MobileActivity).getBleDevice()!!

        gatt = BleManager.getInstance().getBluetoothGatt(bleDevice)

        openCloseNotification(true)


    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            openCloseNotification(false)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun postInit() {

    }

    override fun initObserver() {

    }


    override fun handleListener() {
        mBinding.incAppBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        mBinding.btnSetConfig.setOnClickListener {

            when (editTextChecker(
                requireContext(), "Main Floor", mBinding.edtMainFloor.text.toString()
            ) && editTextChecker(
                requireContext(), "Fire Man Floor", mBinding.edtFireManFloor.text.toString()
            ) && editTextChecker(
                requireContext(), "Home Landing Time", mBinding.edtHomeLandingTime.text.toString()
            ) && editTextChecker(
                requireContext(), "Home Landing Floor", mBinding.edtHomeLandingFloor.text.toString()
            )) {
                true -> {
                    val mainFloorValue = decimalToHex(mBinding.edtMainFloor.text.toString())

                    val firemanFloorValue = decimalToHex(mBinding.edtFireManFloor.text.toString())

                    val homeLandingTimeValue = largeDecimalToHex(mBinding.edtHomeLandingTime.text.toString())

                    val homeLandingFloorValue =
                        decimalToHex(mBinding.edtHomeLandingFloor.text.toString())

                    val basicConfigVal =
                        "090302$mainFloorValue$firemanFloorValue${homeLandingTimeValue}${homeLandingFloorValue}EF"

                    Log.i("HexStringWrite", basicConfigVal.toString())

                     writeData(basicConfigVal) {
                         Toast.makeText(requireContext(), "Config Set Successfully", Toast.LENGTH_SHORT).show()
                     }
                }
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
                    CoroutineScope(Main).launch {
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
                    CoroutineScope(Main).launch {
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
                        CoroutineScope(Main).launch {
                            Toast.makeText(requireContext(), "Notify Success", Toast.LENGTH_SHORT)
                                .show()
                        }

                        readData()

//                    runOnUiThread(Runnable { addText(txt, "notify success") })
                    }

                    override fun onNotifyFailure(exception: BleException) {
                        CoroutineScope(Main).launch {
                            Toast.makeText(requireContext(), "Notify Failure", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }


                    override fun onCharacteristicChanged(data: ByteArray) {

                        CoroutineScope(Main).launch {

                            val hexString = HexUtil.formatHexString(data)
                            mBinding.edtMainFloor.setText(hexToDecimal("${hexString[7]}"))
                            mBinding.edtFireManFloor.setText(hexToDecimal("${hexString[9]}"))
                            val homeLandingTimeVal = hexToDecimal("${hexString[10]}${hexString[11]}") + hexToDecimal("${hexString[12]}${hexString[13]}") + hexToDecimal("${hexString[14]}${hexString[15]}")
                            mBinding.edtHomeLandingTime.setText(homeLandingTimeVal)
                            mBinding.edtHomeLandingFloor.setText(hexToDecimal("${hexString[17]}"))

                            mBinding.initResponseVal.text = HexUtil.formatHexString(data).toString()

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
                writeData(ReadRequestConstants.BasicConfig) { writeSuccess ->
                    if (writeSuccess) {
                        //openCloseNotification(true)
                    } else {
                        Toast.makeText(
                            requireContext(), "Can't Connect to Basic Config ", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}