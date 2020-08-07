package com.sbizzera.real_estate_manager.ui.rem_activity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.sbizzera.real_estate_manager.R

class ChooseUserMaterialDialog : DialogFragment() {

    private lateinit var listener: DialogDismissListener
    private var dialogView: View? = null

    fun setListener(dialogDismissListener: DialogDismissListener) {
        listener = dialogDismissListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_choose_user, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        return dialogBuilder.apply {
            dialogBuilder.setPositiveButton("OK", null)
            dialogBuilder.setTitle("CHOOSE YOUR USERNAME")
//            dialogBuilder.setMessage("Username is mandatory to use Real Estate Manager")
            dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)
            dialogView?.let {
                onViewCreated(it, savedInstanceState)
            }
            setView(dialogView)

        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDialogDismiss(dialogView?.findViewById<TextInputEditText>(R.id.username_edt)?.text.toString())
    }

    interface DialogDismissListener {
        fun onDialogDismiss(userName: String)
    }

    override fun getView(): View? {
        return dialogView
    }
}