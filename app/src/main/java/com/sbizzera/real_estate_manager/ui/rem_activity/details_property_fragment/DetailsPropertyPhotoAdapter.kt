package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyPhotoAdapter.DetailsPhotoViewHolder
import kotlinx.android.synthetic.main.photo_in_detail_item.view.*

class DetailsPropertyPhotoAdapter : RecyclerView.Adapter<DetailsPhotoViewHolder>() {

    var photoList: List<DetailsPhotoUiState> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsPhotoViewHolder {
        return DetailsPhotoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_in_detail_item, parent, false)
        )
    }

    override fun getItemCount() = photoList.size

    override fun onBindViewHolder(holder: DetailsPhotoViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class DetailsPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun bind(position: Int) {
            with(itemView) {
                Glide.with(this).load(photoList[position].photoUri).into(photo_img)
                photo_title_txt.text = photoList[position].photoTitle
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}