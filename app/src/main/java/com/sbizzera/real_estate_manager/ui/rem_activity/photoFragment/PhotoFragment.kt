package com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.EventListener
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*
import java.io.File
import java.io.IOException
import java.util.*

class PhotoFragment() : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_FROM_GALLERY = 2
    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoId :String
    lateinit var listener : EventListener

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

        val viewModel = ViewModelProvider(this,ViewModelFactory).get(PhotoFragmentViewModel::class.java)

        viewModel.viewAction.observe(this){viewAction ->
            when(viewAction){
                is ViewAction.SavePhoto -> println("debug : save clicked")
                is ViewAction.LaunchGallery -> listener.onLaunchGalleryClick()
                is ViewAction.LaunchCamera -> listener.onLaunchCameraClick()
            }
        }

        view.takePhotoBtn.setOnClickListener {
            viewModel.takePhotoBtnClicked()
        }
        view.addPhotoFromGalleryBtn.setOnClickListener {
            viewModel.addPhotoFromGalleryBtnClicked()
        }
        view.savePhotoBtn.setOnClickListener {
            viewModel.savePhotoBtnClicked()
        }

        //TODO se faire expliquer ça par Nino et comment le mettre en MVVM
//        view.takePhotoBtn.setOnClickListener {
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
//                    val photoFile: File? = try {
//                        createImageFile()
//                    } catch (ex: IOException) {
//                        null
//                    }
//
//                    photoFile?.also {
//                        val photoURI: Uri = FileProvider.getUriForFile(
//                            requireActivity(),
//                            "com.sbizzera.real_estate_manager",
//                            it
//                        )
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                    }
//                }
//            }
//        }


//        view.addPhotoFromGalleryBtn.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent,REQUEST_IMAGE_FROM_GALLERY)
//        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            photoContainer.setImageURI(currentPhotoPath.toUri())
            val testPhotoId = "032da1bd-3150-4009-a4fa-330ae2e70f3a3018680426709714817"
            photoContainer.setImageURI("/data/user/0/com.sbizzera.real_estate_manager/files/${testPhotoId}.jpg".toUri())

        }

        if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == AppCompatActivity.RESULT_OK) {
            photoContainer.setImageURI(data?.data)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File? = activity!!.filesDir
        currentPhotoId = UUID.randomUUID().toString()
        return File.createTempFile(
            currentPhotoId,
            ".jpg",
            storageDir
        )
            .apply {
            currentPhotoPath = absolutePath
        }
    }
}