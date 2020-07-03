package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyPhotoAdapter.DetailsPhotoViewHolder

class DetailsPropertyPhotoAdapter: RecyclerView.Adapter<DetailsPhotoViewHolder>() {
    
    private lateinit var photoList : List<PhotoUiModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsPhotoViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: DetailsPhotoViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    
    
    inner class DetailsPhotoViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        
    }
}