package com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditUiState
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_editor.*
import kotlinx.android.synthetic.main.fragment_photo_editor.view.*



class PhotoEditorFragment() : Fragment() {

    private lateinit var viewModel: EditPropertyViewModel

    private lateinit var photoImg: ImageView
    private lateinit var photoTitle: TextView

    companion object {
        fun newInstance(): PhotoEditorFragment = PhotoEditorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_photo_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        photoImg = view.current_photo_img
        photoTitle = view.photo_title_edit_text
        viewModel = ViewModelProvider(this, ViewModelFactory).get(EditPropertyViewModel::class.java)
        viewModel.photoEditorViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                EditPropertyViewModel.PhotoEditorViewAction.TitleEmptyError -> {
                    Snackbar.make(
                        view.photo_editor_fragment_container,
                        "Please give a title to this Photo",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                EditPropertyViewModel.PhotoEditorViewAction.CloseFragment -> {
                    hideKeyboard()
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }

            }
        }

        updateUi(viewModel.currentPhoto)
        delete_photo_btn.setOnClickListener {
            viewModel.onDeletePhotoInEditor()
        }
        view.save_photo_btn.setOnClickListener {
            viewModel.onSavePhotoInEditor(photoTitle.text.toString())
        }
    }


    private fun updateUi(photo: EditUiState.PhotoInEditUiState) {
        Glide.with(photoImg).load(photo.photoUri).into(photoImg)
        photoTitle.text = photo.photoTitle
    }

    private fun hideKeyboard() {
        photoTitle.clearFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            photoTitle.windowToken,
            0
        )
    }


}