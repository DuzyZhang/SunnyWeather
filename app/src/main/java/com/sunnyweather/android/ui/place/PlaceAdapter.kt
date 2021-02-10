package com.sunnyweather.android.ui.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.databinding.PlaceItemBinding
import com.sunnyweather.android.logic.network.Place

class PlaceAdapter(private val fragment: Fragment,private val placeList: List<Place>):
        RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder( val itemBinding: PlaceItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context)
                ,parent,false)
        return ViewHolder(itemBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val place = placeList[position]
        holder.itemBinding.placeName.text = place.name
        holder.itemBinding.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size


}