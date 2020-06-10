package com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.SelectPhotoSourceListener
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*

class PhotoFragment() : Fragment(), OnPhotoSelectedListener {


    private lateinit var viewModel: PhotoFragmentViewModel
    lateinit var listener: SelectPhotoSourceListener

    companion object {
        fun newInstance(): PhotoFragment {
            return PhotoFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_photo, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory).get(PhotoFragmentViewModel::class.java)
        viewModel.viewAction.observe(this) { viewAction ->
            when (viewAction) {
                is ViewAction.OnLaunchGalleryClick -> listener.onLaunchGalleryClick()
                is ViewAction.OnLaunchCameraClick -> listener.onLaunchCameraClick()
            }
        }

        viewModel.uiModelLiveData.observe(this) { model ->
            updateUi(model)
        }

        view.takePhotoBtn.setOnClickListener {
            viewModel.onLaunchCameraClick()
        }
        view.addPhotoFromGalleryBtn.setOnClickListener {
            viewModel.onLaunchGalleryClick()
        }
        view.savePhotoBtn.setOnClickListener {
            viewModel.onSavePhotoBtnClicked()
        }

        view.photoTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.onTitleTextChanged(text)
        }

        return view
    }


    private fun updateUi(model: UiModel) {
        savePhotoBtn.isClickable = model.saveBtnClickable
        savePhotoBtn.isVisible = model.saveBtnClickable
        Glide.with(this).load(model.imageUri).into(photoContainer)
    }


    override fun onPhotoSelected(uri: String) {
        viewModel.onPhotoSelected(uri)
    }
}