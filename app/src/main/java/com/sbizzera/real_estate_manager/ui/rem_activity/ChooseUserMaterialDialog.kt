package com.sbizzera.real_estate_manager.ui.rem_activity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.utils.ViewModelFactory

class ChooseUserMaterialDialog : DialogFragment() {

    private var dialogView: View? = null
    private lateinit var viewModel: REMActivityViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_choose_user, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(REMActivityViewModel::class.java)
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        return dialogBuilder.apply {
            dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)
            dialogView?.let {
                onViewCreated(it, savedInstanceState)
            }
            setView(dialogView)
            dialogBuilder.setTitle("CHOOSE YOUR USERNAME")
            dialogBuilder.setPositiveButton("OK",null)

        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.setUserNameInSharedPrefs(dialogView?.findViewById<TextInputEditText>(R.id.username_edt)?.text.toString())
    }


    override fun getView(): View? {
        return dialogView
    }
}