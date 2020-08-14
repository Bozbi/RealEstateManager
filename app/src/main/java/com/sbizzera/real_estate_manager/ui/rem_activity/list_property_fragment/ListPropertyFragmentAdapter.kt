package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPropertyClickEvent
import com.sbizzera.real_estate_manager.events.OnPropertyClickEventListenable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_property_item_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class ListPropertyFragmentAdapter : RecyclerView.Adapter<ListPropertyFragmentAdapter.ViewHolder>(),
    OnPropertyClickEventListenable {

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
                Picasso.get().load(
                    "file://${propertyModel.photoUri}"
                ).resize(300,300).centerCrop().into(this.property_img, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        if (e is FileNotFoundException) {
                            CoroutineScope(IO).launch {
                                delay(200)
                                withContext(Main){
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                })
                setOnClickListener {
                    onPropertyClickListener.onPropertyItemClick(propertyModel.id)
                }
                @Suppress("ResourceAsColor")
                list_item_container.setBackgroundColor(propertyModel.backGroundColor)
            }
        }
    }

    override fun setListener(listener: OnPropertyClickEvent) {
        onPropertyClickListener = listener
    }
}