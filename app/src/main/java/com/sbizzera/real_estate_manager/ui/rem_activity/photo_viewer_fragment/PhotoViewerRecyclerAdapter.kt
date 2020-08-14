package com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment

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
import kotlinx.android.synthetic.main.photo_in_viewer_item.view.*

class PhotoViewerRecyclerAdapter : RecyclerView.Adapter<PhotoViewerRecyclerAdapter.ViewHolder>() {

    var photoList = listOf<PhotoInViewer>()

    private lateinit var onViewHolderBoundListener: OnViewHolderBound

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_in_viewer_item,parent,false)
        )
    }

    override fun getItemCount() = photoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setListener(onViewHolderBoundListener: OnViewHolderBound){
        this.onViewHolderBoundListener = onViewHolderBoundListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int){
            with(itemView){
                Glide.with(photo_in_details_img).load(photoList[position].uri).listener(object :RequestListener<Drawable>{
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
                }).into(photo_in_details_img)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo_in_details_img.transitionName = "transition$position"
                }
            }
        }
    }

    interface OnViewHolderBound{
        fun onViewHolderBound(position:Int)
    }
}