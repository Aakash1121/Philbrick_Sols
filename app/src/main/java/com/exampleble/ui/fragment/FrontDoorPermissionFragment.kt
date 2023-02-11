package com.exampleble.ui.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import android.widget.Toast
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
import com.exampleble.databinding.FragmentFrontDoorPermissionBinding
import com.exampleble.ui.MobileActivity
import com.exampleble.ui.adapters.DoorPermissionsConfigAdapter
import com.exampleble.ui.models.FloorDisplayConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FrontDoorPermissionFragment : BaseFragment() {
    lateinit var mBinding: FragmentFrontDoorPermissionBinding
    lateinit var doorPermissionsAdapter: DoorPermissionsConfigAdapter
    private var doorPermissionList = arrayListOf<FloorDisplayConfig>()

    private lateinit var bleDevice: BleDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var gattService: BluetoothGattService
    private lateinit var gattCharacteristic: BluetoothGattCharacteristic
    private var isInitial = false

    override fun getInflateResource(): Int {
        return R.layout.fragment_front_door_permission
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

        mBinding.incAppBar.txtDesc.text = "Front Door Permission"
        doorPermissionsAdapter = object : DoorPermissionsConfigAdapter(requireContext()) {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                doorPermissionsAdapter.getData()?.get(position)?.isSelected =
                    doorPermissionsAdapter.getData()?.get(position)?.isSelected != true
            }
        }

        mBinding.rvFloors.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFloors.adapter = doorPermissionsAdapter

        doorPermissionList.add(FloorDisplayConfig("0", "Zeroth", false))
        doorPermissionList.add(FloorDisplayConfig("1", "First", false))
        doorPermissionList.add(FloorDisplayConfig("2", "Second", false))
        doorPermissionList.add(FloorDisplayConfig("3", "Third", false))
        doorPermissionList.add(FloorDisplayConfig("4", "Fourth", false))
        doorPermissionList.add(FloorDisplayConfig("5", "Fifth", false))
        doorPermissionList.add(FloorDisplayConfig("6", "Sixth", false))
        doorPermissionList.add(FloorDisplayConfig("7", "Seventh", false))
        doorPermissionList.add(FloorDisplayConfig("8", "Eighth", false))
        doorPermissionList.add(FloorDisplayConfig("9", "Ninth", false))
        doorPermissionList.add(FloorDisplayConfig("10", "Tenth", false))
        doorPermissionList.add(FloorDisplayConfig("11", "Eleventh", false))
        doorPermissionList.add(FloorDisplayConfig("12", "Twelfth", false))
        doorPermissionList.add(FloorDisplayConfig("13", "Thirteen", false))
        doorPermissionList.add(FloorDisplayConfig("14", "Fourteen", false))
        doorPermissionList.add(FloorDisplayConfig("15", "Fifteen", false))
        doorPermissionsAdapter.updateAll(doorPermissionList)
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

            val selectedList = StringBuffer()
            for (i in doorPermissionList) {
                if (i.isSelected) {
                    selectedList.append("01")
                } else {
                    selectedList.append("00")
                }
            }


            val frontDoorConfigVal = "130304${selectedList}EF"

            Log.i("HexStringWrite", frontDoorConfigVal.toString())

            writeData(frontDoorConfigVal) {
                Toast.makeText(
                    requireContext(),
                    "Config Set Successfully",
                    Toast.LENGTH_SHORT
                ).show()
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

                            val selectedStr = hexString.substring(6, hexString.length - 2)
                            Log.i("selectedString", selectedStr)

                            for ((index, i) in (1 until selectedStr.length step 2).withIndex()) {
                                doorPermissionList[index].isSelected = selectedStr[i] == '1'
                            }
                            doorPermissionsAdapter.updateAll(doorPermissionList)

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
                writeData(ReadRequestConstants.DoorPermissionFront) { writeSuccess ->
                    if (writeSuccess) {
                        //openCloseNotification(true)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Can't Connect to Front Door Config ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


}