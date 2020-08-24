package com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.PhotoOnEdit
import com.sbizzera.real_estate_manager.utils.architecture_components.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_editor.*
import kotlinx.android.synthetic.main.fragment_photo_editor.view.*


class PhotoEditorFragment : Fragment() {

    private lateinit var viewModel: PhotoEditorViewModel

    companion object {
        fun newInstance(): PhotoEditorFragment = PhotoEditorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory
        ).get(PhotoEditorViewModel::class.java)

        updateUi(viewModel.currentPhotoEdited)

        initViewActions(view)

        initListeners()
    }

    private fun initViewActions(view: View) {
        viewModel.photoEditorViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                PhotoEditorViewModel.PhotoEditorViewAction.TitleEmptyError -> {
                    Snackbar.make(
                        view.photo_editor_fragment_container,
                        "Please give a title to this Photo",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                PhotoEditorViewModel.PhotoEditorViewAction.CloseFragment -> {
                    hideKeyboard()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }
    }

    private fun initListeners() {
        delete_photo_btn.setOnClickListener {
            viewModel.onDeletePhotoInEditor()
        }

        save_photo_btn.setOnClickListener {
            viewModel.onSavePhotoInEditor(photo_title_edit_text.text.toString())
        }
    }


    private fun updateUi(photo: PhotoOnEdit) {
        Glide.with(this).load(photo.photoUri).apply(RequestOptions().override(600, 600)).into(current_photo_img)
        photo_title_edit_text.setText(photo.photoTitle)
    }

    private fun hideKeyboard() {
        photo_title_edit_text.clearFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            photo_title_edit_text.windowToken,
            0
        )
    }


}