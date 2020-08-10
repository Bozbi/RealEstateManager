package com.sbizzera.real_estate_manager.utils.custom_views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class MyCustomAutocompleteTextView : AppCompatAutoCompleteTextView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    var listener: TextWatcher? = null

    fun updateIfDifferent(text: CharSequence?) {
        if (this.text != text) {
            removeTextChangedListener(listener)
            setText(text)
            addTextChangedListener(listener)
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
            listener = watcher
    }
}