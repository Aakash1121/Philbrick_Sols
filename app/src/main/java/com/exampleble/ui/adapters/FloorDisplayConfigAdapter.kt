package com.exampleble.ui.adapters

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.exampleble.R
import com.exampleble.databinding.ItemFloorsAdapterBinding
import com.exampleble.ui.models.FloorDisplayConfig
import kotlinx.android.synthetic.main.item_floors_adapter.view.*

class FloorDisplayConfigAdapter(val context: Context,var arrayList:ArrayList<FloorDisplayConfig>) : RecyclerView.Adapter<FloorDisplayConfigAdapter.CustomViewHolder>() {


    inner class CustomViewHolder(private val binding : ItemFloorsAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(floorDisplayConfig : FloorDisplayConfig){
            binding.floorDisplay = floorDisplayConfig
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(ItemFloorsAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (position % 2 != 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#B9B9B9"))
        } else {
            holder.itemView.background = null
        }
        holder.bind(arrayList[position])

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}

