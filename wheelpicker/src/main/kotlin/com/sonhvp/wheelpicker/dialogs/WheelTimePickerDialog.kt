package com.sonhvp.wheelpicker.dialogs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.sonhvp.wheelpicker.R
import com.sonhvp.wheelpicker.WheelTimePicker

class WheelTimePickerDialog(private val context: Context) {

    private var positiveListener: ((hour: Int, minute: Int) -> Unit)? = null
    private var negativeListener: (() -> Unit)? = null
    private var dismissListener: (() -> Unit)? = null
    private var positiveText: String? = null
    private var negativeText: String? = null

    fun onDismiss(dismissListener: (() -> Unit)? = null) {
        this.dismissListener = dismissListener
    }

    fun onNegative(text: String? = null, negativeListener: (() -> Unit)? = null) {
        this.negativeText = text
        this.negativeListener = negativeListener
    }

    fun onPositive(text: String? = null, positiveListener: ((hour: Int, minute: Int) -> Unit)? = null) {
        this.positiveText = text
        this.positiveListener = positiveListener
    }

    private fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_wtp, null)
        val wheelTimePicker = view.findViewById<WheelTimePicker>(R.id.dialog_wheelPicker)
        AlertDialog.Builder(context).apply {
            setView(view)
            //setTitle("Title")
            setOnDismissListener {
                dismissListener?.invoke()
            }
            setNegativeButton(negativeText ?: context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                negativeListener?.invoke()
            }
            setPositiveButton(positiveText ?: context.getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                wheelTimePicker.run {
                    positiveListener?.invoke(getSelectedHour(), getSelectedMinute())
                }
            }
        }.show()
    }

    companion object {
        fun show(context: Context, block: WheelTimePickerDialog.() -> Unit) {
            WheelTimePickerDialog(context).apply(block).show()
        }
    }

}