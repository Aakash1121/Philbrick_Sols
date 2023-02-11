package com.exampleble.common

import android.content.Context
import android.widget.Toast

class ReusedMethod {

    companion object {

        fun navigateToFragment(
            fragmentManager: androidx.fragment.app.FragmentManager,
            yourPlaceholder: Int,
            fragment: androidx.fragment.app.Fragment,
            addToBackStack: Boolean,
        ) {

            val transaction = fragmentManager.beginTransaction()

            transaction.replace(yourPlaceholder, fragment)


            if (addToBackStack) transaction.addToBackStack("")
            transaction.commit()
        }

        fun stringToHex(ba: ByteArray): String {
            val str = StringBuilder()
            for (i in ba.indices) str.append(String.format("%x", ba[i]))
            return str.toString()
        }

        fun hexToString(hex: String): String {
            val str = java.lang.StringBuilder()
            var i = 0
            while (i < hex.length) {
                str.append(hex.substring(i, i + 2).toInt(16).toChar())
                i += 2
            }
            return str.toString()
        }

        fun editTextChecker(context: Context, editTextName:String, editTextValue:String):Boolean{
            if(editTextValue.isEmpty()){
                Toast.makeText(context, "Please Enter $editTextName", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun decimalToHex(str:String):String{
            return "0${java.lang.Integer.toHexString(str.toInt()).toString().uppercase()}"
        }

        fun largeDecimalToHex(str:String): String {
            val homeLandingTimeValue = StringBuffer()
            str.forEach { c ->
                homeLandingTimeValue.append(decimalToHex(c.toString()))
            }
            return homeLandingTimeValue.toString()

        }

        fun hexToDecimal(hex:String):String{
            return Integer.parseInt(hex,16).toString()
        }
    }
}