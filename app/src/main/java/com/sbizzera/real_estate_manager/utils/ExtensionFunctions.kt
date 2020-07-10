package com.sbizzera.real_estate_manager.utils

import android.widget.AutoCompleteTextView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText

 fun TextInputEditText.updateTextIfDifferent(textToSet: String) {
    if (text.toString() != textToSet) {
        this.setText( textToSet)
    }
}

 fun AutoCompleteTextView.updateTypeIfDifferent(textToSet: String) {
    if (text.toString() != textToSet) {
        this.setText( textToSet)
    }
}

fun Chip.updateIsChecked(isChecked : Boolean){
    if(this.isChecked != isChecked()){
        this.isChecked = isChecked
    }
}