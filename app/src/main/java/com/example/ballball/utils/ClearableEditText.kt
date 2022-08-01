package com.example.ballball.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.ballball.R
import com.google.android.material.textfield.TextInputEditText

object ClearableEditText {
    fun EditText.makeClearableEditText(
        onIsNotEmpty: (() -> Unit)?,
        onClear: (() -> Unit)?,
        clearDrawable: Drawable
    ) {
        val updateRightDrawable = {
            this.setCompoundDrawables(null, null,
                if (text.isNotEmpty()) clearDrawable else null,
                null)
        }
        updateRightDrawable()

        this.afterTextChanged {
            if (it.isNotEmpty()) {
                onIsNotEmpty?.invoke()
            }
            updateRightDrawable()
        }
        this.onRightDrawableClicked {
            this.text.clear()
            this.setCompoundDrawables(null, null, null, null)
            onClear?.invoke()
            this.requestFocus()
        }
    }

    private val COMPOUND_DRAWABLE_RIGHT_INDEX = 2

    fun EditText.makeClearableEditText(onIsNotEmpty: (() -> Unit)?, onCleared: (() -> Unit)?) {
        compoundDrawables[COMPOUND_DRAWABLE_RIGHT_INDEX]?.let { clearDrawable ->
            makeClearableEditText(onIsNotEmpty, onCleared, clearDrawable)
        }
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }
}