package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyPhotoAdapter.DetailsPhotoViewHolder
import kotlinx.android.synthetic.main.photo_in_detail_item.view.*

class DetailsPropertyPhotoAdapter : RecyclerView.Adapter<DetailsPhotoViewHolder>() {

    private lateinit var onPhotoClickForTransitionListener: OnPhotoClickForTransitionListener
    private lateinit var onViewHolderBoundListener: OnViewHolderBoundListener
    private var photoList: List<DetailsPhotoUiState> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsPhotoViewHolder {
        return DetailsPhotoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_in_detail_item, parent, false)
        )
    }

    override fun getItemCount() = photoList.size

    override fun onBindViewHolder(holder: DetailsPhotoViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setListener(
        onPhotoClickForTransitionListener: OnPhotoClickForTransitionListener,
        onViewHolderBoundListener: OnViewHolderBoundListener
    ) {
        this.onPhotoClickForTransitionListener = onPhotoClickForTransitionListener
        this.onViewHolderBoundListener = onViewHolderBoundListener
    }

    fun setList(photoList: List<DetailsPhotoUiState>) {
        this.photoList = photoList
    }

    inner class DetailsPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            with(itemView) {
                Glide.with(this).load(photoList[position].photoUri).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onViewHolderBoundListener.onViewHolderBound(position)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onViewHolderBoundListener.onViewHolderBound(position)
                        return false
                    }
                }).into(photo_img)
                photo_title_txt.text = photoList[position].photoTitle
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo_img.transitionName = "transition$position"
                }
                photo_img.setOnClickListener {
                    onPhotoClickForTransitionListener.onPhotoClickedForTransition(adapterPosition, it)
                }
            }
        }

    }

    interface OnPhotoClickForTransitionListener {
        fun onPhotoClickedForTransition(position: Int, transitionView: View)
    }

    interface OnViewHolderBoundListener {
        fun onViewHolderBound(position: Int)
    }
}