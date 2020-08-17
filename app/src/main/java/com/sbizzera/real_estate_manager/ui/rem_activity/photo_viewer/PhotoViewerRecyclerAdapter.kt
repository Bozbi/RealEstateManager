package com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_in_viewer_item.view.*
import java.lang.Exception

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
                Picasso.get().load("file://${photoList[position].uri}").resize(1000,1000).centerCrop().into(photo_in_details_img,object : Callback{
                    override fun onSuccess() {
                        onViewHolderBoundListener.onViewHolderBound(position)
                    }

                    override fun onError(e: Exception?) {
                        onViewHolderBoundListener.onViewHolderBound(position)
                    }
                })
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