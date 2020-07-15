package com.sbizzera.real_estate_manager.utils.custom_views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class MyCustomEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    private var textWatcher: TextWatcher? = null

    fun updateIfDifferent(text: CharSequence?) {
        if (this.text?.toString() != text?.toString()) {
            removeTextChangedListener(textWatcher)
            setText(text)
            addTextChangedListener(textWatcher)
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
        textWatcher = watcher
    }

}