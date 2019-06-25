package com.sonhvp.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sonhvp.wheelpicker.dialogs.WheelTimePickerDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_wheelPicker.onTimeSelected = { hour, minute ->
            Log.d("WheelPicker", "hour: $hour \tminute: $minute")
        }

        main_wheelPickerDialog.setOnClickListener {
            WheelTimePickerDialog.show(this) {
                onNegative {

                }
                onPositive { hour, minute ->
                    Log.d("WheelPicker", "hour: $hour \tminute: $minute")
                }
            }
        }
    }

}