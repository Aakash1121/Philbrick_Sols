package com.exampleble.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.exampleble.R
import com.exampleble.ui.models.FloorDisplayConfig
import kotlinx.android.synthetic.main.item_floors_adapter.view.*

open class FloorDisplayConfigAdapter(val context: Context) :
    BaseRecyclerViewAdapter<FloorDisplayConfig>(context) {
    private var lastSelectedItem = -1
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.item_floors_adapter
    }


    override fun setData(holder: RecyclerView.ViewHolder, data: FloorDisplayConfig, position: Int) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(position, data)
    }

    open inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position: Int? = 0

        init {

            itemView.setOnClickListener {
                onItemClick(position!!)
                lastSelectedItem = position!!
                notifyDataSetChanged()
            }
        }

        open fun bindData(position: Int, mData: FloorDisplayConfig) {
            this.position = position

            if (mData.isSelected || lastSelectedItem == position) {

                itemView.setBackgroundColor(Color.parseColor("#00AFEF"))
                mData.isSelected = false
            } else {
                if(position%2!=0){
                    itemView.setBackgroundColor(Color.parseColor("#B9B9B9"))
                }else{
                    itemView.background = null
                }

            }
            itemView.txtFloorNoVal.text = mData.floorNo
            itemView.txtFloorNameVal.text = mData.floorName

        }
    }

    open fun onItemClick(position: Int) {

    }

}