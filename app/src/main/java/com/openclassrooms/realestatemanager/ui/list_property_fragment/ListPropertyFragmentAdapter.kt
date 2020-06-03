package com.openclassrooms.realestatemanager.ui.list_property_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.Property
import kotlinx.android.synthetic.main.list_property_item_view.view.*

class ListPropertyFragmentAdapter : RecyclerView.Adapter<ListPropertyFragmentAdapter.ViewHolder>() {

    var list :List<Property> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_property_item_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.propertyTitle.text = list[position].propertyTitle
    }


    inner class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView)
}