package com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.photo.Photo
import kotlinx.android.synthetic.main.property_add_photo_item_view.view.*
import kotlinx.android.synthetic.main.property_photo_item_view.view.*

class NewPropertyPhotoRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listPhotos: List<Photo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.property_photo_item_view->{
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.property_photo_item_view, parent, false)
                PropertyPhotoViewHolder(view)
            }
            R.layout.property_add_photo_item_view->{
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.property_add_photo_item_view, parent, false)
                AddPhotoViewHolder(view)
            }
            else -> throw IllegalArgumentException("unknown viewHolder type")
        }
    }

    override fun getItemCount(): Int = listPhotos.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.property_photo_item_view -> {
                (holder as PropertyPhotoViewHolder).bind(listPhotos[position])
            }
            R.layout.property_add_photo_item_view -> {
                (holder as AddPhotoViewHolder).bind()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            listPhotos.size -> R.layout.property_add_photo_item_view
            else -> R.layout.property_photo_item_view
        }
    }

    inner class PropertyPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(photo : Photo){
            Glide.with(itemView.photo_img).load(photo.uri).into(itemView.photo_img)
        }
    }

    inner class AddPhotoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(){

        }
    }


}