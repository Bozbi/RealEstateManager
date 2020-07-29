package com.sbizzera.real_estate_manager.ui.rem_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sbizzera.real_estate_manager.R
import kotlinx.android.synthetic.main.rational_permission_dialog.*

class RationalePermissionDialog : DialogFragment() {

    companion object{
        fun newInstance() = RationalePermissionDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rational_permission_dialog,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button.setOnClickListener {
            dismiss()
        }
    }
}