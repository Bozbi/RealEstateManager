package com.sbizzera.real_estate_manager.ui.rem_activity.details_property

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property.DetailsPropertyPhotoAdapter.DetailsPhotoViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_in_detail_item.view.*
import java.lang.Exception

class DetailsPropertyPhotoAdapter : ListAdapter<DetailsPhotoUiState,DetailsPhotoViewHolder>(
    DiffUtilCallback
) {

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
                Picasso.get().load("file://${photoList[position].photoUri}").resize(500,500).centerCrop().into(photo_in_details_img,object : Callback{
                    override fun onSuccess() {
                        onViewHolderBoundListener.onViewHolderBound(position)
                    }

                    override fun onError(e: Exception?) {
                        onViewHolderBoundListener.onViewHolderBound(position)
                    }

                })
                photo_title_txt.text = photoList[position].photoTitle
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo_in_details_img.transitionName = "transition$position"
                }
                photo_in_details_img.setOnClickListener {
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

    private object DiffUtilCallback : DiffUtil.ItemCallback<DetailsPhotoUiState>(){
        override fun areItemsTheSame(oldItem: DetailsPhotoUiState, newItem: DetailsPhotoUiState) =
            oldItem.photoTitle ==newItem.photoTitle


        override fun areContentsTheSame(oldItem: DetailsPhotoUiState, newItem: DetailsPhotoUiState)=
            oldItem ==newItem

    }
}