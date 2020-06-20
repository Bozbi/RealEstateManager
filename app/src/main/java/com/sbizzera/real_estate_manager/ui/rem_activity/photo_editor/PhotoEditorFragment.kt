package com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.ViewAction.CloseFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.ViewAction.TitleEmptyError
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_editor.*
import kotlinx.android.synthetic.main.fragment_photo_editor.view.*


class PhotoEditorFragment : Fragment() {

    private lateinit var viewModel: NewPropertyFragmentViewModel

    companion object {
        fun newInstance(): PhotoEditorFragment = PhotoEditorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_editor, container, false)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(NewPropertyFragmentViewModel::class.java)
        viewModel.currentPhotoEditedLD.observe(this){ currentPhoto->
            updateUi(currentPhoto.first)
        }
        viewModel.photoEditorViewAction.observe(this){viewAction ->
            when(viewAction){
                CloseFragment -> {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
                TitleEmptyError->{
                    title_input_layout.isErrorEnabled = true
                    title_input_layout.error ="Title should not be empty"
                }
            }
        }

        view.save_photo_btn.setOnClickListener {
            photo_title_edt.clearFocus()
            viewModel.onSaveButtonClicked(photo_title_edt.text.toString())
        }
        view.delete_photo_btn.setOnClickListener {
            viewModel.onDeleteButtonClicked()
        }
        return view
    }

    private fun updateUi(photo:Photo) {
        Glide.with(current_photo_img).load(photo.uri).into(current_photo_img)
        photo_title_edt.setText(photo.title)
    }

}