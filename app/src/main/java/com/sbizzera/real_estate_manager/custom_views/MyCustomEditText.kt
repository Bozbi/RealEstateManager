package com.sbizzera.real_estate_manager.custom_views

import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
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

    fun afterTextChangedDelayed(afterTextChanged : (String)->Unit){
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null
            override fun afterTextChanged(text: Editable?) {
                timer?.cancel()
                timer = object :CountDownTimer(2000,2000){
                    override fun onFinish() {
                        afterTextChanged.invoke(text.toString())
                    }
                    override fun onTick(millisUntilFinished: Long) {}
                }.start()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}