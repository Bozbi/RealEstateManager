package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.events.OnPhotoEditClickListener
import kotlinx.android.synthetic.main.property_photo_item_view.view.*

class EditPropertyPhotoRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    lateinit var listPhotos: List<EditPropertyUiModel.PhotoUiModel>
    lateinit var listener: OnPhotoEditClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_photo_item_view, parent, false)
        return PropertyPhotoViewHolder(view)
    }

    override fun getItemCount(): Int = listPhotos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PropertyPhotoViewHolder-> holder.bind(listPhotos[position])
        }
    }


    inner class PropertyPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: EditPropertyUiModel.PhotoUiModel) {
            Glide.with(itemView.photo_img).load(photo.photoUri).into(itemView.photo_img)
            itemView.photo_title.text = photo.photoTitle
            itemView.edit_btn.setOnClickListener {
                listener.onPhotoEditClick(adapterPosition)
            }
        }
    }

}