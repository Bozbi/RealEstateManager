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
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.PhotoEditorViewAction.CloseFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.PhotoEditorViewAction.TitleEmptyError
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_editor.view.*


class PhotoEditorFragment() : Fragment() {

    private lateinit var viewModel: NewPropertyFragmentViewModel

    private lateinit var photoImg: ImageView
    private lateinit var photoTitle: TextView

    companion object {
        fun newInstance(): PhotoEditorFragment = PhotoEditorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_editor, container, false)
        photoImg = view.current_photo_img
        photoTitle = view.photo_title_edit_text
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(NewPropertyFragmentViewModel::class.java)
        viewModel.photoEditorViewAction.observe(this) { viewAction ->
            when (viewAction) {
                TitleEmptyError -> {
                    Snackbar.make(
                        view.photo_editor_fragment_container,
                        "Please give a title to this Photo",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                CloseFragment -> {
                    hideKeyboard()
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }

            }
        }

        updateUi(viewModel.currentPhoto)

        view.delete_photo_btn.setOnClickListener {
            viewModel.onDeletePhotoInEditor()
        }

        view.save_photo_btn.setOnClickListener {
            viewModel.onSavePhotoInEditor(photoTitle.text.toString())
        }


        return view
    }

    private fun updateUi(photo: Photo) {
        Glide.with(photoImg).load(photo.uri).into(photoImg)
        photoTitle.text = photo.title
    }

    private fun hideKeyboard() {
        photoTitle.clearFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            photoTitle.windowToken,
            0
        )
    }


}