package com.exampleble.ui.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.exampleble.R
import com.exampleble.common.ReadRequestConstants
import com.exampleble.common.ReusedMethod
import com.exampleble.databinding.FragmentFloorDisplayConfigBinding
import com.exampleble.ui.MobileActivity
import com.exampleble.ui.adapters.FloorDisplayConfigAdapter
import com.exampleble.ui.models.FloorDisplayConfig
import kotlinx.android.synthetic.main.item_floors_adapter.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor

class FloorDisplayConfigFragment : BaseFragment() {

    private lateinit var mBinding: FragmentFloorDisplayConfigBinding
    private var floorList = arrayListOf<FloorDisplayConfig>()
    private lateinit var floorDisplayConfigAdapter: FloorDisplayConfigAdapter
    private var currentlySelectedPosition = -1

    private lateinit var bleDevice: BleDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var gattService: BluetoothGattService
    private lateinit var gattCharacteristic: BluetoothGattCharacteristic
    private var isInitial = false


    override fun getInflateResource(): Int {
        return R.layout.fragment_floor_display_config
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        mBinding = getBinding()
        setViews()


        bleDevice = (activity as MobileActivity).getBleDevice()!!

        gatt = BleManager.getInstance().getBluetoothGatt(bleDevice)

        openCloseNotification(true)
    }


    private fun setViews() {
        mBinding.incAppBar.txtDesc.text = "Floor Display Config"

        setAdapter()

    }

    private fun setAdapter(){
        floorList.add(FloorDisplayConfig("0", "", false))
        floorList.add(FloorDisplayConfig("1", "", false))
        floorList.add(FloorDisplayConfig("2", "", false))
        floorList.add(FloorDisplayConfig("3", "", false))
        floorList.add(FloorDisplayConfig("4", "", false))
        floorList.add(FloorDisplayConfig("5", "", false))
        floorList.add(FloorDisplayConfig("6", "", false))
        floorList.add(FloorDisplayConfig("7", "", false))
        floorList.add(FloorDisplayConfig("8", "", false))
        floorList.add(FloorDisplayConfig("9", "", false))
        floorList.add(FloorDisplayConfig("10", "", false))
        floorList.add(FloorDisplayConfig("11", "", false))
        floorList.add(FloorDisplayConfig("12", "", false))
        floorList.add(FloorDisplayConfig("13", "", false))
        floorList.add(FloorDisplayConfig("14", "", false))
        floorList.add(FloorDisplayConfig("15", "", false))

        floorDisplayConfigAdapter = FloorDisplayConfigAdapter(requireContext(),floorList)

        mBinding.rvFloors.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFloors.adapter = floorDisplayConfigAdapter

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
            requireActivity().onBackPressed()
        }



        mBinding.btnSetConfig.setOnClickListener {


            val currentVal = StringBuffer()

            Log.i("FloorData", floorList.toString())
            for(i in floorList){
                val str = if(i.floorName.length == 1) "0${i.floorName.trim()}"
                else i.floorName.trim()
                currentVal.append(str)
            }

            val strByteArray = currentVal.toString().toByteArray()

            val floorDisplayHexVal = ReusedMethod.stringToHex(strByteArray)
            val floorDisplayConfigVal = "230301${floorDisplayHexVal}EF"

            Log.i("HexStringWrite", floorDisplayConfigVal.toString())

            writeData(floorDisplayConfigVal) {
                Toast.makeText(requireContext(), "Config Set Successfully", Toast.LENGTH_SHORT).show()
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
                            mBinding.initResponseVal.text = hexString

                            val selectedStr = hexString.substring(6, hexString.length - 2)
                            Log.i("selectedString", selectedStr)

                            for ((index, i) in (selectedStr.indices step 4).withIndex()) {
                                val currentVal = StringBuffer()
                                currentVal.append(selectedStr[i])
                                currentVal.append(selectedStr[i + 1])
                                currentVal.append(selectedStr[i + 2])
                                currentVal.append(selectedStr[i + 3])
                                floorList[index].floorName = ReusedMethod.hexToString(currentVal.toString())
                            }

                            floorDisplayConfigAdapter.notifyDataSetChanged()

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
                writeData(ReadRequestConstants.FloorDisplayConfig) { writeSuccess ->
                    if (writeSuccess) {
                        //openCloseNotification(true)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Can't Connect to Floor Display Config",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

}