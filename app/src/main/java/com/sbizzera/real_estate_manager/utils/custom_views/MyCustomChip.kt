package com.sbizzera.real_estate_manager.utils.custom_views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip

class MyCustomChip : Chip {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    private var listener: OnCheckedChangeListener? = null

    fun updateIfDifferent(isChecked: Boolean) {
        if (this.isChecked != isChecked) {
            setOnCheckedChangeListener(null)
            this.isChecked = isChecked
            setOnCheckedChangeListener(listener)
        }
    }

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        super.setOnCheckedChangeListener(listener)
        if (listener != null) {
            this.listener = listener
        }
    }
}