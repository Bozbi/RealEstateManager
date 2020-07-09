package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPropertyClickEvent
import com.sbizzera.real_estate_manager.events.OnPropertyClickEventListenable
import kotlinx.android.synthetic.main.list_property_item_view.view.*

class ListPropertyFragmentAdapter : RecyclerView.Adapter<ListPropertyFragmentAdapter.ViewHolder>(),OnPropertyClickEventListenable {

    lateinit var onPropertyClickListener: OnPropertyClickEvent
    var list: List<ListPropertyItemUiState> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_property_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            with(itemView) {
                val propertyModel = list[position]
                property_title.text = propertyModel.title
                property_type.text = propertyModel.type
                property_price.text = propertyModel.price
                Glide.with(this).load(propertyModel.photoUri).into(itemView.property_img)
                setOnClickListener {
                    onPropertyClickListener.onPropertyItemClick(propertyModel.id)
                }
            }
        }
    }

    override fun setListener(listener: OnPropertyClickEvent) {
        onPropertyClickListener =listener
    }
}