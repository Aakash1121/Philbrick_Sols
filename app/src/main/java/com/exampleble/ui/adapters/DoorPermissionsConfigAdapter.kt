package com.exampleble.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.exampleble.R
import com.exampleble.ui.models.FloorDisplayConfig
import kotlinx.android.synthetic.main.item_door_permissions_adapter.view.*

open class DoorPermissionsConfigAdapter(val context: Context) :
    BaseRecyclerViewAdapter<FloorDisplayConfig>(context) {
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.item_door_permissions_adapter
    }


    override fun setData(holder: RecyclerView.ViewHolder, data: FloorDisplayConfig, position: Int) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(position, data)
    }

    open inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position: Int? = 0

        open fun bindData(position: Int, mData: FloorDisplayConfig) {
            this.position = position
            itemView.txtFloorNoVal.text = mData.floorNo
            if (position % 2 != 0) {
                itemView.setBackgroundColor(Color.parseColor("#B9B9B9"))
            } else {
                itemView.background = null
            }

            itemView.chkBoxFloorPermission.setOnClickListener {
                onItemClick(position)
            }
            itemView.chkBoxFloorPermission.isChecked = mData.isSelected
        }
    }

    open fun onItemClick(position: Int) {

    }

}