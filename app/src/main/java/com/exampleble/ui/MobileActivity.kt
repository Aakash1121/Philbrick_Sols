package com.exampleble.ui

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleDevice
import com.exampleble.ui.bluetoothfragment.CharacteristicOperationFragment1
import com.exampleble.ui.bluetoothfragment.MainFragment1
import com.exampleble.R
import com.exampleble.databinding.ActivityMobileBinding
import com.exampleble.ui.bluetoothfragment.CharacteristicListFragment1
import com.exampleble.ui.bluetoothfragment.ServiceListFragment1
import com.exampleble.observers.Observer
import com.exampleble.observers.ObserverManager
import java.util.ArrayList

class MobileActivity : BaseActivity(), Observer, MainFragment1.OnFragmentInteractionListener {
    private lateinit var mBinding: ActivityMobileBinding
    private var titles = arrayOfNulls<String>(3)
    private var bleDevice: BleDevice? = null
    private var bluetoothGattService: BluetoothGattService? = null
    private var characteristic: BluetoothGattCharacteristic? = null
    private var charaProp: Int = 0
    private var currentPage = 0
    private val fragments = ArrayList<Fragment>()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

     var isBLEConnected = false


    override fun getResource(): Int {
        return R.layout.activity_mobile
    }

  /*  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentPage != 0) {
                currentPage--
                changePage(currentPage)
                true
            } else {

                true
            }
        } else super.onKeyDown(keyCode, event)
    }*/

    override fun initView() {
        mBinding = getBinding()

        val navHostHomeFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostHomeFragment.navController


        fragments.add(MainFragment1.newInstance("", ""))

//        initPage()
        ObserverManager.getInstance().addObserver(this)
    }

    override fun initProgressBar() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun showLoadingIndicator(isShow: Boolean) {

    }

    override fun displayMessage(message: String) {

    }


    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().clearCharacterCallback(bleDevice)
        ObserverManager.getInstance().deleteObserver(this)
        BleManager.getInstance().disconnect(bleDevice)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun getBleDevice(): BleDevice? {
        return this.bleDevice
    }

    fun getBluetoothGattService(): BluetoothGattService? {
        return bluetoothGattService
    }

    fun setBluetoothGattService(bluetoothGattService: BluetoothGattService) {
        this.bluetoothGattService = bluetoothGattService
    }

    fun getCharacteristic(): BluetoothGattCharacteristic? {
        return characteristic
    }

    fun setCharacteristic(characteristic: BluetoothGattCharacteristic) {
        this.characteristic = characteristic
    }

    fun getCharaProp(): Int {
        return charaProp
    }

    fun setCharaProp(charaProp: Int) {
        this.charaProp = charaProp
    }

    override fun disConnected(device: BleDevice?) {
        if (device != null && bleDevice != null && device.key == bleDevice!!.key) {
            //finish()
            Toast.makeText(
                this,
                "Connection Disconnected",
                Toast.LENGTH_LONG
            ).show()
            isBLEConnected = false
        }
    }

    override fun onFragmentInteraction(bledevice: BleDevice?) {
        this.bleDevice = bledevice
//        prepareFragment(1)
//        changePage(1)
    }

    private fun updateFragment(position: Int) {
        if (position > fragments.size - 1) {
            return
        }
        for (i in fragments.indices) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = fragments[i]
            if (i == position) {
                transaction.show(fragment)
            } else {
                transaction.remove(fragment)
            }
            transaction.commit()
        }
    }

    fun changePage(page: Int) {
        currentPage = page
//        updateFragment(page)
        if (currentPage == 2) {
            navigateToFragment(supportFragmentManager,R.id.nav_host_fragment, fragments[page],true)
            (fragments[2] as CharacteristicListFragment1).showData()
        } else if (currentPage == 3) {
            navigateToFragment(supportFragmentManager,R.id.nav_host_fragment, fragments[page],true)
            (fragments[3] as CharacteristicOperationFragment1).showData()
        }
    }

    fun initPage() {
        // prepareFragment()
        //onNotify()
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
////        fragmentTransaction.replace(R.id.nav_host_fragment, fragments[0])
////        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
////        fragmentTransaction.commit()

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

    private fun prepareFragment(page:Int) {

        fragments.add(ServiceListFragment1())
        fragments.add(CharacteristicListFragment1())
        fragments.add(CharacteristicOperationFragment1())

        navigateToFragment(supportFragmentManager,R.id.nav_host_fragment, fragments[page],true)

    }

}